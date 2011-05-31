package edu.ualberta.med.biobank.widgets.trees.infos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.PagedDialog.NewListener;
import edu.ualberta.med.biobank.dialogs.StudyAliquotedSpecimenDialog;
import edu.ualberta.med.biobank.dialogs.StudySourceSpecimenDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.trees.infos.listener.IInfoTreeAddItemListener;
import edu.ualberta.med.biobank.widgets.trees.infos.listener.IInfoTreeDeleteItemListener;
import edu.ualberta.med.biobank.widgets.trees.infos.listener.IInfoTreeEditItemListener;
import edu.ualberta.med.biobank.widgets.trees.infos.listener.InfoTreeEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SourceToAliquotedTypeEntryInfoTree extends
    SourceToAliquotedTypeInfoTree {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SourceToAliquotedTypeEntryInfoTree.class.getName());

    private List<SpecimenTypeWrapper> availableSpecimenTypes;

    private List<SourceSpecimenWrapper> selectedSourceSpecimen;

    private List<SourceSpecimenWrapper> addedOrModifiedSourceSpecimen;
    private List<AliquotedSpecimenWrapper> addedOrModifiedAliquotedSpecimen;

    private List<SourceSpecimenWrapper> deletedSourceSpecimen;
    private List<AliquotedSpecimenWrapper> removedAliquotedSpecimen;

    private StudyWrapper study;

    public SourceToAliquotedTypeEntryInfoTree(Composite parent,
        List<SourceSpecimenWrapper> collection) {
        super(parent, collection);
    }

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param style the style of control to construct
     * @param sampleStorageCollection the sample storage already selected and to
     *            be displayed in the table viewer (can be null).
     * @param toolkit The toolkit is responsible for creating SWT controls
     *            adapted to work in Eclipse forms. If widget is not used in a
     *            form this parameter should be null.
     */
    public SourceToAliquotedTypeEntryInfoTree(Composite parent,
        StudyWrapper study) {
        super(parent, null);
        this.study = study;
        initSpecimenTypes();
        selectedSourceSpecimen = study.getSourceSpecimenCollection(true);
        if (selectedSourceSpecimen == null) {
            selectedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        }
        setCollection(selectedSourceSpecimen);
        addedOrModifiedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        addedOrModifiedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();
        deletedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        removedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addSourceSpecimen() {
        SourceSpecimenWrapper newSourceSpecimen = new SourceSpecimenWrapper(
            SessionManager.getAppService());
        newSourceSpecimen.setStudy(study);
        addOrEditStudySourceSpecimen(true, newSourceSpecimen);
    }

    private void addOrEditStudySourceSpecimen(boolean add,
        final SourceSpecimenWrapper sourceSpecimen) {
        List<SpecimenTypeWrapper> dialogSpecimenTypes = availableSpecimenTypes;
        if (!add) {
            dialogSpecimenTypes.add(sourceSpecimen.getSpecimenType());
        }
        NewListener newListener = null;
        if (add) {
            // only add to the collection when adding and not editing
            newListener = new NewListener() {
                @Override
                public void newAdded(ModelWrapper<?> spec) {
                    SourceSpecimenWrapper ssw = (SourceSpecimenWrapper) spec;
                    ssw.setStudy(study);
                    availableSpecimenTypes.remove(ssw.getSpecimenType());
                    selectedSourceSpecimen.add(ssw);
                    addedOrModifiedSourceSpecimen.add(ssw);
                    reloadCollection(selectedSourceSpecimen);
                    notifyListeners();
                }
            };
        }
        StudySourceSpecimenDialog dlg = new StudySourceSpecimenDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            sourceSpecimen, newListener, dialogSpecimenTypes);

        int res = dlg.open();
        if (res == Dialog.OK) {
            addedOrModifiedAliquotedSpecimen.removeAll(dlg
                .getRemovedAliquotedSpecimen());
            addedOrModifiedAliquotedSpecimen.addAll(dlg
                .getAddedAliquotedSpecimen());
            removedAliquotedSpecimen
                .removeAll(addedOrModifiedAliquotedSpecimen);
            removedAliquotedSpecimen.addAll(dlg.getRemovedAliquotedSpecimen());
        }
        if (!add && res == Dialog.OK) {
            reloadCollection(selectedSourceSpecimen);
            notifyListeners();
        }
        treeViewer.refresh();
    }

    private void addEditSupport() {
        if (SessionManager.canCreate(SourceSpecimenWrapper.class)) {
            addAddItemListener(new IInfoTreeAddItemListener() {
                @Override
                public void addItem(InfoTreeEvent event) {
                    addSourceSpecimen();
                }
            });
        }
        if (SessionManager.canUpdate(SourceSpecimenWrapper.class)) {
            addEditItemListener(new IInfoTreeEditItemListener() {
                @Override
                public void editItem(InfoTreeEvent event) {
                    Object selection = getSelection();
                    if (selection != null)
                        if (selection instanceof SourceSpecimenWrapper) {
                            SourceSpecimenWrapper sourceSpecimen = (SourceSpecimenWrapper) selection;
                            addOrEditStudySourceSpecimen(false, sourceSpecimen);
                        } else if (selection instanceof AliquotedSpecimenWrapper) {
                            AliquotedSpecimenWrapper aliquoted = (AliquotedSpecimenWrapper) selection;
                            editAliquotedSpecimen(aliquoted);
                        }
                }
            });
        }
        if (SessionManager.canDelete(SourceSpecimenWrapper.class)) {
            addDeleteItemListener(new IInfoTreeDeleteItemListener() {
                @Override
                public void deleteItem(InfoTreeEvent event) {
                    Object selection = getSelection();
                    if (selection instanceof SourceSpecimenWrapper) {
                        SourceSpecimenWrapper sourceSpecimen = (SourceSpecimenWrapper) selection;
                        if (sourceSpecimen != null) {
                            if (!MessageDialog
                                .openConfirm(
                                    PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getShell(),
                                    Messages
                                        .getString("SourceSpecimenEntryInfoTable.delete.title"),
                                    Messages
                                        .getString("SourceSpecimenEntryInfoTable.delete.question"))) {
                                return;
                            }

                            selectedSourceSpecimen.remove(sourceSpecimen);
                            setCollection(selectedSourceSpecimen);
                            deletedSourceSpecimen.add(sourceSpecimen);
                            availableSpecimenTypes.add(sourceSpecimen
                                .getSpecimenType());
                            notifyListeners();
                        }
                    } else if (selection instanceof AliquotedSpecimenWrapper) {
                        AliquotedSpecimenWrapper aliquoted = (AliquotedSpecimenWrapper) selection;
                        if (aliquoted != null) {
                            if (!MessageDialog
                                .openConfirm(
                                    PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getShell(),
                                    Messages
                                        .getString("AliquotedSpecimenEntryInfoTable.delete.title"),
                                    Messages
                                        .getString(
                                            "AliquotedSpecimenEntryInfoTable.delete.question",
                                            aliquoted.getSpecimenType()
                                                .getName()))) {
                                return;
                            }
                            // need to do that to find the exact ss wrapper that
                            // is inside the study that will be saved later.
                            for (SourceSpecimenWrapper ssw : study
                                .getSourceSpecimenCollection(false)) {
                                if (ssw.equals(aliquoted.getSourceSpecimen())) {
                                    ssw.removeFromAliquotedSpecimenCollection(Arrays
                                        .asList(aliquoted));
                                }
                            }
                            addedOrModifiedAliquotedSpecimen.remove(aliquoted);
                            removedAliquotedSpecimen.add(aliquoted);
                            treeViewer.refresh();
                            notifyListeners();
                        }
                    }
                }
            });
        }
    }

    private void editAliquotedSpecimen(
        final AliquotedSpecimenWrapper aliquotedSpecimen) {
        List<SpecimenTypeWrapper> dialogTypes = new ArrayList<SpecimenTypeWrapper>();
        SourceSpecimenWrapper sourceSpecimen = aliquotedSpecimen
            .getSourceSpecimen();
        if (sourceSpecimen.getSpecimenType() != null)
            dialogTypes.addAll(sourceSpecimen.getSpecimenType()
                .getChildSpecimenTypeCollection(true));
        for (AliquotedSpecimenWrapper asw : sourceSpecimen
            .getAliquotedSpecimenCollection(false)) {
            dialogTypes.remove(asw.getSpecimenType());
        }
        dialogTypes.add(aliquotedSpecimen.getSpecimenType());
        StudyAliquotedSpecimenDialog dlg = new StudyAliquotedSpecimenDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            aliquotedSpecimen, null, dialogTypes);

        int res = dlg.open();
        if (res == Dialog.OK) {
            treeViewer.refresh();
            notifyListeners();
        }
    }

    private void initSpecimenTypes() {
        try {
            availableSpecimenTypes = SpecimenTypeWrapper
                .getAllSourceOnlySpecimenTypes(SessionManager.getAppService(),
                    false);
            for (SourceSpecimenWrapper ssw : study
                .getSourceSpecimenCollection(false)) {
                availableSpecimenTypes.remove(ssw.getSpecimenType());
            }
        } catch (final RemoteConnectFailureException exp) {
            BiobankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (ApplicationException e) {
            logger.error("initSpecimenTypes", e);
        }
    }

    public List<SourceSpecimenWrapper> getAddedOrModifiedSourceSpecimens() {
        return addedOrModifiedSourceSpecimen;
    }

    public List<SourceSpecimenWrapper> getDeletedSourceSpecimens() {
        return deletedSourceSpecimen;
    }

    public void reload() {
        selectedSourceSpecimen = study.getSourceSpecimenCollection(true);
        if (selectedSourceSpecimen == null) {
            selectedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        }
        reloadCollection(selectedSourceSpecimen);
        addedOrModifiedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        deletedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TreeRowData i1 = getCollectionModelObject(e1);
                    TreeRowData i2 = getCollectionModelObject(e2);
                    return super.compare(i1.name, i2.name);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

    @Override
    public TreeRowData getCollectionModelObject(Object item) throws Exception {
        if (item instanceof SpecimenTypeWrapper) {
            TreeRowData info = new TreeRowData();
            info.type = (SpecimenTypeWrapper) item;
            info.name = info.type.getName();
            return info;
        } else
            return super.getCollectionModelObject(item);
    }

    public List<AliquotedSpecimenWrapper> getAddedOrModifiedAliquotedSpecimens() {
        return addedOrModifiedAliquotedSpecimen;
    }

    public List<AliquotedSpecimenWrapper> getRemovedAliquotedSpecimens() {
        return removedAliquotedSpecimen;
    }
}
