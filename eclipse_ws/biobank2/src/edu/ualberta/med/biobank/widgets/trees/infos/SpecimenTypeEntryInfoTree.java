package edu.ualberta.med.biobank.widgets.trees.infos;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.dialogs.SpecimenTypeDialog;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.trees.infos.listener.IInfoTreeAddItemListener;
import edu.ualberta.med.biobank.widgets.trees.infos.listener.IInfoTreeDeleteItemListener;
import edu.ualberta.med.biobank.widgets.trees.infos.listener.IInfoTreeEditItemListener;
import edu.ualberta.med.biobank.widgets.trees.infos.listener.InfoTreeEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current specimen type collection and allows the user to add
 * additional specimen type to the collection.
 */
public class SpecimenTypeEntryInfoTree extends SpecimenTypeInfoTree {

    private static BgcLogger logger = BgcLogger
        .getLogger(SpecimenTypeEntryInfoTree.class.getName());

    private List<SpecimenTypeWrapper> selectedSpecimenTypes;

    private String addMessage;

    private String editMessage;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param SampleTypeCollection the specimen type already selected and to be
     *            displayed in the table viewer (can be null).
     */
    public SpecimenTypeEntryInfoTree(Composite parent,
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

    private void addOrEditSpecimenType(boolean add,
        SpecimenTypeWrapper specimenType, String message) {
        SpecimenTypeDialog dlg = new SpecimenTypeDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            specimenType, message, selectedSpecimenTypes);
        if (dlg.open() == Dialog.OK) {
            if (addEditOk(specimenType)) {
                try {
                    specimenType.persist();
                    if (add) {
                        // only add to the collection when adding and not
                        // editing
                        selectedSpecimenTypes.add(specimenType);
                    }
                } catch (Exception e) {
                    BgcPlugin.openAsyncError("Save Failed", e);
                }
                reloadCollection(selectedSpecimenTypes);
            } else {
                try {
                    specimenType.reload();
                } catch (Exception e) {
                    BgcPlugin.openAsyncError("Refresh Failed", e);
                }
                reloadCollection(selectedSpecimenTypes);
            }
        }
    }

    private void addEditSupport() {
        addAddItemListener(new IInfoTreeAddItemListener() {
            @Override
            public void addItem(InfoTreeEvent event) {
                addSpecimenType();
            }
        });

        addEditItemListener(new IInfoTreeEditItemListener() {
            @Override
            public void editItem(InfoTreeEvent event) {
                SpecimenTypeWrapper type = getSelection();
                if (type != null)
                    addOrEditSpecimenType(false, type, editMessage);
            }
        });

        addDeleteItemListener(new IInfoTreeDeleteItemListener() {
            @Override
            public void deleteItem(InfoTreeEvent event) {
                SpecimenTypeWrapper specType = getSelection();
                if (specType != null) {
                    try {
                        if (!specType.isNew() && specType.isUsedBySpecimens()) {
                            BgcPlugin
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
                        BgcPlugin.openRemoteConnectErrorMessage(exp);
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
                    && (sv.getName().equals(type.getName()) || sv
                        .getNameShort().equals(type.getNameShort())))
                    throw new BiobankCheckException(
                        "That specimen type has already been added.");
        } catch (BiobankException bce) {
            BgcPlugin.openAsyncError("Check error", bce);
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
            BgcPlugin.openAsyncError("AppService unavailable", e);
        }
    }

    @SuppressWarnings("serial")
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
