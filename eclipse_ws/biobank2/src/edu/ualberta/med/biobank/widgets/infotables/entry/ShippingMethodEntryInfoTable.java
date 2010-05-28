package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.ShippingMethodDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.ShippingMethodInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class ShippingMethodEntryInfoTable extends ShippingMethodInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ShippingMethodEntryInfoTable.class.getName());

    private List<ShippingMethodWrapper> selectedShippingMethods;

    private List<ShippingMethodWrapper> addedOrModifiedShippingMethods;

    private List<ShippingMethodWrapper> deletedShippingMethods;

    private String addMessage;

    private String editMessage;

    private SiteWrapper currentSite;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param SampleTypeCollection the sample storage already selected and to be
     *            displayed in the table viewer (can be null).
     */
    public ShippingMethodEntryInfoTable(Composite parent,
        List<ShippingMethodWrapper> globalShippingMethods, String addMessage,
        String editMessage, SiteWrapper currentSite) {
        super(parent, null);
        setLists(globalShippingMethods);
        this.addMessage = addMessage;
        this.editMessage = editMessage;
        this.currentSite = currentSite;
        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    /**
     * 
     * @param message The message to display in the SampleTypeDialog.
     */
    public void addShippingMethod() {
        ShippingMethodWrapper newST = new ShippingMethodWrapper(SessionManager
            .getAppService());
        addOrEditShippingMethod(true, newST, addMessage);
    }

    private boolean addOrEditShippingMethod(boolean add,
        ShippingMethodWrapper shippingMethod, String message) {
        ShippingMethodDialog dlg = new ShippingMethodDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            shippingMethod, message);
        if (dlg.open() == Dialog.OK) {
            if (addEditOk(shippingMethod)) {
                if (add) {
                    // only add to the collection when adding and not editing
                    selectedShippingMethods.add(shippingMethod);
                }
                reloadCollection(selectedShippingMethods);
                addedOrModifiedShippingMethods.add(shippingMethod);
                notifyListeners();
                return true;
            } else {
                ShippingMethodWrapper orig = dlg.getOrigShippingMethod();
                shippingMethod.setName(orig.getName());
                reloadCollection(selectedShippingMethods);
            }
        }
        return false;
    }

    private void addEditSupport() {
        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                addShippingMethod();
            }
        });

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                ShippingMethodWrapper type = getSelection();
                if (type != null)
                    addOrEditShippingMethod(false, type, editMessage);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                ShippingMethodWrapper shippingMethod = getSelection();
                if (shippingMethod != null) {
                    try {
                        if (!shippingMethod.isNew() && shippingMethod.isUsed()) {
                            BioBankPlugin
                                .openError(
                                    "Shipping Method Delete Error",
                                    "Cannot delete shipping method \""
                                        + shippingMethod.getName()
                                        + "\" since created shipments are using it.");
                            return;
                        }

                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), "Delete Shipping Method",
                            "Are you sure you want to delete shipping method \""
                                + shippingMethod.getName() + "\"?")) {
                            return;
                        }

                        // equals method now compare toString() results if both
                        // ids are null.
                        selectedShippingMethods.remove(shippingMethod);

                        setCollection(selectedShippingMethods);
                        deletedShippingMethods.add(shippingMethod);
                        notifyListeners();
                    } catch (final RemoteConnectFailureException exp) {
                        BioBankPlugin.openRemoteConnectErrorMessage();
                    } catch (Exception e) {
                        logger.error("BioBankFormBase.createPartControl Error",
                            e);
                    }
                }
            }
        });
    }

    private boolean addEditOk(ShippingMethodWrapper type) {
        try {
            type.checkUnique();
        } catch (BiobankCheckException bce) {
            BioBankPlugin.openAsyncError("Check error", bce);
            return false;
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("Check error", e);
            return false;
        }
        return true;
    }

    public List<ShippingMethodWrapper> getAddedOrModifiedShippingMethods() {
        return addedOrModifiedShippingMethods;
    }

    public List<ShippingMethodWrapper> getDeletedShippingMethods() {
        return deletedShippingMethods;
    }

    public void setLists(List<ShippingMethodWrapper> ShippingMethodCollection) {
        if (ShippingMethodCollection == null) {
            selectedShippingMethods = new ArrayList<ShippingMethodWrapper>();
        } else {
            selectedShippingMethods = new ArrayList<ShippingMethodWrapper>(
                ShippingMethodCollection);
        }
        reloadCollection(ShippingMethodCollection);
        addedOrModifiedShippingMethods = new ArrayList<ShippingMethodWrapper>();
        deletedShippingMethods = new ArrayList<ShippingMethodWrapper>();
    }

    public SiteWrapper getCurrentSite() {
        return currentSite;
    }

    public void reload() {
        try {
            setLists(ShippingMethodWrapper.getShippingMethods(SessionManager
                .getAppService()));
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("AppService unavailable", e);
        }
    }
}
