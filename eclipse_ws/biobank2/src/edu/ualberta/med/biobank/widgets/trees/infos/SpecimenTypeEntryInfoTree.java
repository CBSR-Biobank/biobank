package edu.ualberta.med.biobank.widgets.trees.infos;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeDeleteAction;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeGetAllAction;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeSaveAction;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.dialogs.SpecimenTypeDialog;
import edu.ualberta.med.biobank.forms.BiobankFormBase;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.SpecimenType;
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
                    SpecimenTypeSaveAction save =
                        new SpecimenTypeSaveAction(specimenType.getName(),
                            specimenType.getNameShort());
                    save.setId(specimenType.getId());
                    specimenType.setId(SessionManager.getAppService().doAction(
                        save).getId());
                    if (add) {
                        // only add to the collection when adding and not
                        // editing
                        selectedSpecimenTypes.add(specimenType);
                    }
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        Messages.SpecimenTypeEntryInfoTree_save_error_title, e);
                }
                reloadCollection(selectedSpecimenTypes);
            } else {
                try {
                    specimenType.reload();
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        Messages.SpecimenTypeEntryInfoTree_refresh_error_title,
                        e);
                }
                reloadCollection(selectedSpecimenTypes);
            }
        }
    }

    private void addEditSupport() {
        addAddItemListener(new IInfoTreeAddItemListener<SpecimenTypeWrapper>() {
            @Override
            public void addItem(InfoTreeEvent<SpecimenTypeWrapper> event) {
                addSpecimenType();
            }
        });

        addEditItemListener(new IInfoTreeEditItemListener<SpecimenTypeWrapper>() {
            @Override
            public void editItem(InfoTreeEvent<SpecimenTypeWrapper> event) {
                SpecimenTypeWrapper type = getSelection();
                if (type != null)
                    addOrEditSpecimenType(false, type, editMessage);
            }
        });

        addDeleteItemListener(new IInfoTreeDeleteItemListener<SpecimenTypeWrapper>() {
            @Override
            public void deleteItem(InfoTreeEvent<SpecimenTypeWrapper> event) {
                SpecimenTypeWrapper specType = getSelection();
                if (specType == null) return;

                try {
                    specType.reload();
                    if (!specType.isNew() && specType.isUsed()) {
                        BgcPlugin
                            .openError(
                                Messages.SpecimenTypeEntryInfoTree_delete_error_title,
                                NLS.bind(
                                    Messages.SpecimenTypeEntryInfoTree_delete_error_msg,
                                    specType.getName()));
                        return;
                    }

                    if (!MessageDialog
                        .openConfirm(
                            PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow().getShell(),
                            Messages.SpecimenTypeEntryInfoTree_delete_question_title,
                            NLS.bind(
                                Messages.SpecimenTypeEntryInfoTree_delete_question_msg,
                                specType.getName()))) {
                        return;
                    }

                    // equals method now compare toString() results if both
                    // ids are null.
                    selectedSpecimenTypes.remove(specType);
                    needReload.addAll(specType
                        .getParentSpecimenTypeCollection(false));
                    SessionManager.getAppService().doAction(
                        new SpecimenTypeDeleteAction(specType
                            .getWrappedObject()));
                    reloadCollection(selectedSpecimenTypes);
                } catch (ApplicationException e) {
                    if (e.getCause() instanceof ConstraintViolationException) {
                        List<String> msgs = BiobankFormBase
                            .getConstraintViolationsMsgs(
                            (ConstraintViolationException) e.getCause());
                        BgcPlugin
                            .openAsyncError(
                                Messages.SpecimenTypeEntryInfoTree_delete_type_error_msg,
                                StringUtils.join(msgs, "\n"));

                    } else {
                        BgcPlugin
                            .openAsyncError(
                                Messages.SpecimenTypeEntryInfoTree_delete_type_error_msg,
                                e.getLocalizedMessage());
                    }
                } catch (final RemoteConnectFailureException exp) {
                    BgcPlugin.openRemoteConnectErrorMessage(exp);
                } catch (Exception e) {
                    BgcPlugin
                        .openAsyncError(
                            Messages.SpecimenTypeEntryInfoTree_delete_type_error_msg,
                            e);
                }
            }
        });
    }

    private boolean addEditOk(SpecimenTypeWrapper type) {
        try {
            for (SpecimenTypeWrapper sv : selectedSpecimenTypes)
                if (!sv.getId().equals(type.getId())) {
                    if (sv.getName().equals(type.getName())) {
                        throw new BiobankCheckException(
                            NLS.bind(
                                Messages.SpecimenTypeEntryInfoTree_name_already_added_error_msg,
                                type.getName()));
                    }
                    else if (sv.getNameShort().equals(type.getNameShort())) {
                        throw new BiobankCheckException(
                            NLS.bind(
                                Messages.SpecimenTypeEntryInfoTree_name_short_already_added_error_msg,
                                type.getNameShort()));
                    }
                }

        } catch (BiobankException bce) {
            BgcPlugin.openAsyncError(
                Messages.SpecimenTypeEntryInfoTree_check_error_title, bce);
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
            List<SpecimenType> globalSpecimenTypes =
                SessionManager.getAppService().doAction(
                    new SpecimenTypeGetAllAction()).getList();
            Assert.isNotNull(globalSpecimenTypes);
            setLists(ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(),
                globalSpecimenTypes, SpecimenTypeWrapper.class));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                Messages.SpecimenTypeEntryInfoTree_unaivalable_error_title, e);
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
