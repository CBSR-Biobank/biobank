package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.dialogs.ShippingMethodDialog;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.ShippingMethodInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current shipping method collection and allows the user to add
 * additional shipping method to the collection.
 */
public class ShippingMethodEntryInfoTable extends ShippingMethodInfoTable {

    private static BgcLogger logger = BgcLogger
        .getLogger(ShippingMethodEntryInfoTable.class.getName());

    List<ShippingMethodWrapper> selectedShippingMethod;

    private String addMessage;

    private String editMessage;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param SampleTypeCollection the shipping method already selected and to
     *            be displayed in the table viewer (can be null).
     */
    public ShippingMethodEntryInfoTable(Composite parent,
        List<ShippingMethodWrapper> globalShippingMethod, String addMessage,
        String editMessage) {
        super(parent, null);
        setLists(globalShippingMethod);
        this.addMessage = addMessage;
        this.editMessage = editMessage;
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
        ShippingMethodWrapper newST = new ShippingMethodWrapper(
            SessionManager.getAppService());
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
                    selectedShippingMethod.add(shippingMethod);
                }
                try {
                    shippingMethod.persist();
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        Messages.ShippingMethodEntryInfoTable_save_error_title,
                        e);
                }
                reloadCollection(selectedShippingMethod);
                return true;
            } else {
                try {
                    shippingMethod.reload();
                } catch (Exception e) {
                    BgcPlugin
                        .openAsyncError(
                            Messages.ShippingMethodEntryInfoTable_refresh_error_title,
                            e);
                }
                reloadCollection(selectedShippingMethod);
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
                ShippingMethodWrapper type = getSelection();
                if (type != null) {
                    try {
                        if (!type.isNew() && type.isUsed()) {
                            BgcPlugin
                                .openError(
                                    Messages.ShippingMethodEntryInfoTable_delete_error_title,
                                    NLS.bind(
                                        Messages.ShippingMethodEntryInfoTable_delete_error_msg,
                                        type.getName()));
                            return;
                        }

                        if (!MessageDialog
                            .openConfirm(
                                PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getShell(),
                                Messages.ShippingMethodEntryInfoTable_delete_confirm_title,
                                NLS.bind(
                                    Messages.ShippingMethodEntryInfoTable_delete_confirm_msg,
                                    type.getName()))) {
                            return;
                        }

                        // equals method now compare toString() results if both
                        // ids are null.
                        selectedShippingMethod.remove(type);
                        type.delete();
                        setCollection(selectedShippingMethod);
                    } catch (final RemoteConnectFailureException exp) {
                        BgcPlugin.openRemoteConnectErrorMessage(exp);
                    } catch (Exception e) {
                        logger.error("BioBankFormBase.createPartControl Error", //$NON-NLS-1$
                            e);
                    }
                }
            }
        });
    }

    private boolean addEditOk(ShippingMethodWrapper type) {
        try {
            for (ShippingMethodWrapper sv : selectedShippingMethod)
                if (!sv.getId().equals(type.getId())
                    && sv.getName().equals(type.getName()))
                    throw new BiobankCheckException(
                        Messages.ShippingMethodEntryInfoTable_already_added_error_msg);
        } catch (BiobankException bce) {
            BgcPlugin.openAsyncError(
                Messages.ShippingMethodEntryInfoTable_check_error_title, bce);
            return false;
        }
        return true;
    }

    public void setLists(List<ShippingMethodWrapper> shippingMethodCollection) {
        selectedShippingMethod = shippingMethodCollection;
        reloadCollection(shippingMethodCollection);
    }

    public void reload() {
        try {
            setLists(ShippingMethodWrapper.getShippingMethods(SessionManager
                .getAppService()));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                Messages.ShippingMethodEntryInfoTable_unavailable_error_title,
                e);
        }
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                return super.compare(((ShippingMethodWrapper) e1).getName(),
                    ((ShippingMethodWrapper) e2).getName());
            }
        };
    }
}
