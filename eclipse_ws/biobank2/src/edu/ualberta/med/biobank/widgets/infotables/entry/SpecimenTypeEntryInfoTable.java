package edu.ualberta.med.biobank.widgets.infotables.entry;

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
import edu.ualberta.med.biobank.dialogs.SpecimenTypeDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenTypeInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current specimen type collection and allows the user to add
 * additional specimen type to the collection.
 */
public class SpecimenTypeEntryInfoTable extends SpecimenTypeInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SpecimenTypeEntryInfoTable.class.getName());

    List<SpecimenTypeWrapper> selectedSpecimenTypes;

    private String addMessage;

    private String editMessage;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param SampleTypeCollection the specimen type already selected and to be
     *            displayed in the table viewer (can be null).
     */
    public SpecimenTypeEntryInfoTable(Composite parent,
        List<SpecimenTypeWrapper> globalSpecimenTypes, String addMessage,
        String editMessage) {
        super(parent, null);
        setLists(globalSpecimenTypes);
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
    public void addSpecimenType() {
        SpecimenTypeWrapper newST = new SpecimenTypeWrapper(
            SessionManager.getAppService());
        addOrEditSpecimenType(true, newST, addMessage);
    }

    private boolean addOrEditSpecimenType(boolean add,
        SpecimenTypeWrapper specimenType, String message) {
        SpecimenTypeDialog dlg = new SpecimenTypeDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            specimenType, message);
        if (dlg.open() == Dialog.OK) {
            if (addEditOk(specimenType)) {
                if (add) {
                    // only add to the collection when adding and not editing
                    selectedSpecimenTypes.add(specimenType);
                }
                try {
                    specimenType.persist();
                } catch (Exception e) {
                    BiobankPlugin.openAsyncError("Save Failed", e);
                }
                reloadCollection(selectedSpecimenTypes);
                return true;
            } else {
                try {
                    specimenType.reload();
                } catch (Exception e) {
                    BiobankPlugin.openAsyncError("Refresh Failed", e);
                }
                reloadCollection(selectedSpecimenTypes);
            }
        }
        return false;
    }

    private void addEditSupport() {
        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                addSpecimenType();
            }
        });

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                SpecimenTypeWrapper type = getSelection();
                if (type != null)
                    addOrEditSpecimenType(false, type, editMessage);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                SpecimenTypeWrapper specType = getSelection();
                if (specType != null) {
                    try {
                        if (!specType.isNew() && specType.isUsedBySpecimens()) {
                            BiobankPlugin
                                .openError(
                                    "Specimen Type Delete Error",
                                    "Cannot delete specimen type \""
                                        + specType.getName()
                                        + "\" since studies and/or patient visits are using it.");
                            return;
                        }

                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), "Delete Specimen Type",
                            "Are you sure you want to delete specimen type \""
                                + specType.getName() + "\"?")) {
                            return;
                        }

                        // equals method now compare toString() results if both
                        // ids are null.
                        selectedSpecimenTypes.remove(specType);
                        specType.delete();
                        setCollection(selectedSpecimenTypes);
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
            for (SpecimenTypeWrapper sv : selectedSpecimenTypes)
                if (!sv.getId().equals(type.getId())
                    && sv.getName().equals(type.getName()))
                    throw new BiobankCheckException(
                        "That specimen type has already been added.");
        } catch (BiobankException bce) {
            BiobankPlugin.openAsyncError("Check error", bce);
            return false;
        }
        return true;
    }

    public void setLists(List<SpecimenTypeWrapper> specimenTypeCollection) {
        selectedSpecimenTypes = specimenTypeCollection;
        reloadCollection(specimenTypeCollection);
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
