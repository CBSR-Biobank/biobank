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
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.dialogs.SourceVesselDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SourceVesselInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class SourceVesselEntryInfoTable extends SourceVesselInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SourceVesselEntryInfoTable.class.getName());

    private List<SourceVesselWrapper> selectedSourceVessels;

    private List<SourceVesselWrapper> addedOrModifiedSourceVessels;

    private List<SourceVesselWrapper> deletedSourceVessels;

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
    public SourceVesselEntryInfoTable(Composite parent,
        List<SourceVesselWrapper> globalSourceVessels, String addMessage,
        String editMessage, SiteWrapper currentSite) {
        super(parent, null);
        setLists(globalSourceVessels);
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
    public void addSourceVessel() {
        SourceVesselWrapper newST = new SourceVesselWrapper(SessionManager
            .getAppService());
        addOrEditSourceVessel(true, newST, addMessage);
    }

    private boolean addOrEditSourceVessel(boolean add,
        SourceVesselWrapper sourceVessel, String message) {
        SourceVesselDialog dlg = new SourceVesselDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            sourceVessel, message);
        if (dlg.open() == Dialog.OK) {
            if (addEditOk(sourceVessel)) {
                if (add) {
                    // only add to the collection when adding and not editing
                    selectedSourceVessels.add(sourceVessel);
                }
                reloadCollection(selectedSourceVessels);
                addedOrModifiedSourceVessels.add(sourceVessel);
                notifyListeners();
                return true;
            } else {
                SourceVesselWrapper orig = dlg.getOrigSourceVessel();
                sourceVessel.setName(orig.getName());
                reloadCollection(selectedSourceVessels);
            }
        }
        return false;
    }

    private void addEditSupport() {
        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                addSourceVessel();
            }
        });

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                SourceVesselWrapper type = getSelection();
                addOrEditSourceVessel(false, type, editMessage);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                SourceVesselWrapper sourceVessel = getSelection();

                try {
                    if (!sourceVessel.isNew() && sourceVessel.isUsed()) {
                        BioBankPlugin
                            .openError(
                                "Source Vessel Delete Error",
                                "Cannot delete source vessel \""
                                    + sourceVessel.getName()
                                    + "\" since studies and/or patient visits are using it.");
                        return;
                    }

                    if (!MessageDialog.openConfirm(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        "Delete Source Vessel",
                        "Are you sure you want to delete source vessel \""
                            + sourceVessel.getName() + "\"?")) {
                        return;
                    }

                    // equals method now compare toString() results if both
                    // ids are null.
                    selectedSourceVessels.remove(sourceVessel);

                    setCollection(selectedSourceVessels);
                    deletedSourceVessels.add(sourceVessel);
                    notifyListeners();
                } catch (final RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    logger.error("BioBankFormBase.createPartControl Error", e);
                }
            }
        });
    }

    private boolean addEditOk(SourceVesselWrapper type) {
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

    public List<SourceVesselWrapper> getAddedOrModifiedSampleTypes() {
        return addedOrModifiedSourceVessels;
    }

    public List<SourceVesselWrapper> getDeletedSampleTypes() {
        return deletedSourceVessels;
    }

    public void setLists(List<SourceVesselWrapper> sourceVesselCollection) {
        if (sourceVesselCollection == null) {
            selectedSourceVessels = new ArrayList<SourceVesselWrapper>();
        } else {
            selectedSourceVessels = new ArrayList<SourceVesselWrapper>(
                sourceVesselCollection);
        }
        reloadCollection(sourceVesselCollection);
        addedOrModifiedSourceVessels = new ArrayList<SourceVesselWrapper>();
        deletedSourceVessels = new ArrayList<SourceVesselWrapper>();
    }

    public SiteWrapper getCurrentSite() {
        return currentSite;
    }

    public void reload() {
        try {
            setLists(SourceVesselWrapper.getAllSourceVessels(SessionManager
                .getAppService()));
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("AppService unavailable", e);
        }
    }
}
