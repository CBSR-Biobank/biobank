package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.dialogs.SpecimentTypeDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenTypeInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class SpecimenTypeEntryInfoTable extends SpecimenTypeInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SpecimenTypeEntryInfoTable.class.getName());

    private List<SpecimenTypeWrapper> selectedSourceVessels;

    private List<SpecimenTypeWrapper> addedOrModifiedSourceVessels;

    private List<SpecimenTypeWrapper> deletedSourceVessels;

    private String addMessage;

    private String editMessage;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param SampleTypeCollection the sample storage already selected and to be
     *            displayed in the table viewer (can be null).
     */
    public SpecimenTypeEntryInfoTable(Composite parent,
        List<SpecimenTypeWrapper> globalSourceVessels, String addMessage,
        String editMessage) {
        super(parent, null);
        setLists(globalSourceVessels);
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
    public void addSourceVessel() {
        SpecimenTypeWrapper newST = new SpecimenTypeWrapper(
            SessionManager.getAppService());
        addOrEditSourceVessel(true, newST, addMessage);
    }

    private boolean addOrEditSourceVessel(boolean add,
        SpecimenTypeWrapper sourceVessel, String message) {
        SpecimentTypeDialog dlg = new SpecimentTypeDialog(PlatformUI
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
                SpecimenTypeWrapper orig = dlg.getOrigSpecimenType();
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
                SpecimenTypeWrapper type = getSelection();
                if (type != null)
                    addOrEditSourceVessel(false, type, editMessage);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                SpecimenTypeWrapper specType = getSelection();
                if (specType != null) {
                    try {
                        if (!specType.isNew() && specType.isUsedBySamples()) {
                            BiobankPlugin
                                .openError(
                                    "Source Vessel Delete Error",
                                    "Cannot delete source vessel \""
                                        + specType.getName()
                                        + "\" since studies and/or patient visits are using it.");
                            return;
                        }

                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), "Delete Source Vessel",
                            "Are you sure you want to delete source vessel \""
                                + specType.getName() + "\"?")) {
                            return;
                        }

                        // equals method now compare toString() results if both
                        // ids are null.
                        selectedSourceVessels.remove(specType);

                        setCollection(selectedSourceVessels);
                        deletedSourceVessels.add(specType);
                        notifyListeners();
                    } catch (final RemoteConnectFailureException exp) {
                        BiobankPlugin.openRemoteConnectErrorMessage(exp);
                    } catch (Exception e) {
                        logger.error("BioBankFormBase.createPartControl Error",
                            e);
                    }
                }
            }
        });
    }

    private boolean addEditOk(SpecimenTypeWrapper type) {
        try {
            for (SpecimenTypeWrapper sv : selectedSourceVessels)
                if (sv.getId() != type.getId()
                    && sv.getName().equals(type.getName()))
                    throw new BiobankCheckException(
                        "That source vessel has already been added.");
            for (SpecimenTypeWrapper sv : addedOrModifiedSourceVessels)
                if (sv.getId() != type.getId()
                    && sv.getName().equals(type.getName()))
                    throw new BiobankCheckException(
                        "That source vessel has already been added.");
            type.checkNameAndShortNameUnique();
        } catch (BiobankException bce) {
            BiobankPlugin.openAsyncError("Check error", bce);
            return false;
        } catch (ApplicationException e) {
            BiobankPlugin.openAsyncError("Check error", e);
            return false;
        }
        return true;
    }

    public List<SpecimenTypeWrapper> getAddedOrModifiedSourceVessels() {
        return addedOrModifiedSourceVessels;
    }

    public List<SpecimenTypeWrapper> getDeletedSourceVessels() {
        return deletedSourceVessels;
    }

    public void setLists(List<SpecimenTypeWrapper> sourceVesselCollection) {
        if (sourceVesselCollection == null) {
            selectedSourceVessels = new ArrayList<SpecimenTypeWrapper>();
        } else {
            selectedSourceVessels = new ArrayList<SpecimenTypeWrapper>(
                sourceVesselCollection);
        }
        reloadCollection(sourceVesselCollection);
        addedOrModifiedSourceVessels = new ArrayList<SpecimenTypeWrapper>();
        deletedSourceVessels = new ArrayList<SpecimenTypeWrapper>();
    }

    public void reload() {
        try {
            setLists(SpecimenTypeWrapper.getAllSpecimenTypes(
                SessionManager.getAppService(), true));
        } catch (ApplicationException e) {
            BiobankPlugin.openAsyncError("AppService unavailable", e);
        }
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                return super.compare(((SpecimenTypeWrapper) e1).getName(),
                    ((SpecimenTypeWrapper) e2).getName());
            }
        };
    }
}
