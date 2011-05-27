package edu.ualberta.med.biobank.widgets.trees.infos;

import java.util.ArrayList;
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

    private List<SourceSpecimenWrapper> deletedSourceSpecimen;

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
        deletedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();

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
                    ((SourceSpecimenWrapper) spec).setStudy(study);
                    availableSpecimenTypes.remove(sourceSpecimen
                        .getSpecimenType());
                    selectedSourceSpecimen.add((SourceSpecimenWrapper) spec);
                    addedOrModifiedSourceSpecimen
                        .add((SourceSpecimenWrapper) spec);
                    reloadCollection(selectedSourceSpecimen);
                    notifyListeners();
                }
            };
        }
        StudySourceSpecimenDialog dlg = new StudySourceSpecimenDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            sourceSpecimen, newListener, dialogSpecimenTypes);

        int res = dlg.open();
        if (!add && res == Dialog.OK) {
            reloadCollection(selectedSourceSpecimen);
            notifyListeners();
        }
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
                            // TODO
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
                        // TODO
                    }
                }
            });
        }
    }

    private void initSpecimenTypes() {
        try {
            availableSpecimenTypes = SpecimenTypeWrapper.getAllSpecimenTypes(
                SessionManager.getAppService(), false);
            List<SourceSpecimenWrapper> sourceSpecimen = study
                .getSourceSpecimenCollection(false);
            if (sourceSpecimen != null) {
                for (SourceSpecimenWrapper ssw : sourceSpecimen) {
                    availableSpecimenTypes.remove(ssw.getSpecimenType());
                }
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

    // @Override
    // protected List<Node> getNodeChildren(Node node) throws Exception {
    // if (node != null && node instanceof BiobankCollectionModel) {
    // BiobankCollectionModel model = (BiobankCollectionModel) node;
    // TreeRowData row = (TreeRowData) model.o;
    // if (row != null)
    // if (row.studySourceVessel != null)
    // return createNodes(node, row.studySourceVessel
    // .getSpecimenType()
    // .getChildSpecimenTypeCollection(false));
    // }
    // return super.getNodeChildren(node);
    // }

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
}
