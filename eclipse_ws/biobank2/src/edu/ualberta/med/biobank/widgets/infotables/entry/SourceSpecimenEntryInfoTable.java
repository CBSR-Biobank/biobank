package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SourceSpecimenInfoTable;

public class SourceSpecimenEntryInfoTable extends SourceSpecimenInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SourceSpecimenEntryInfoTable.class.getName());

    private List<SpecimenTypeWrapper> availableSourceVessels;

    private List<SourceSpecimenWrapper> selectedStudySourceVessels;

    private List<SourceSpecimenWrapper> addedOrModifiedSourceVessels;

    private List<SourceSpecimenWrapper> deletedSourceVessels;

    private StudyWrapper study;

    public SourceSpecimenEntryInfoTable(Composite parent,
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
    public SourceSpecimenEntryInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null);
        this.study = study;
        initSourceVessels();
        selectedStudySourceVessels = study.getSourceSpecimenCollection(true);
        if (selectedStudySourceVessels == null) {
            selectedStudySourceVessels = new ArrayList<SourceSpecimenWrapper>();
        }
        setCollection(selectedStudySourceVessels);
        addedOrModifiedSourceVessels = new ArrayList<SourceSpecimenWrapper>();
        deletedSourceVessels = new ArrayList<SourceSpecimenWrapper>();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addSourceSpecimen() {
        SourceSpecimenWrapper newStudySourcevessel = new SourceSpecimenWrapper(
            SessionManager.getAppService());
        newStudySourcevessel.setStudy(study);
        addOrEditStudySourceVessel(true, newStudySourcevessel);
    }

    private void addOrEditStudySourceVessel(boolean add,
        SourceSpecimenWrapper studySourceVessel) {
        List<SpecimenTypeWrapper> dialogSourceVessels = availableSourceVessels;
        if (!add) {
            dialogSourceVessels.add(studySourceVessel.getSpecimenType());
        }
        // FIXME: create new dialog type
        // StudySourceVesselDialog dlg = new StudySourceVesselDialog(PlatformUI
        // .getWorkbench().getActiveWorkbenchWindow().getShell(),
        // studySourceVessel, dialogSourceVessels);
        // if (dlg.open() == Dialog.OK) {
        // if (add) {
        // // only add to the collection when adding and not editing
        // selectedStudySourceVessels.add(studySourceVessel);
        // }
        // availableSourceVessels.remove(studySourceVessel.getSourceVessel());
        // reloadCollection(selectedStudySourceVessels);
        // addedOrModifiedSourceVessels.add(studySourceVessel);
        // notifyListeners();
        // }
    }

    private void addEditSupport() {
        if (SessionManager.canCreate(SourceSpecimenWrapper.class, null)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    addSourceSpecimen();
                }
            });
        }
        if (SessionManager.canUpdate(SourceSpecimenWrapper.class, null)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    SourceSpecimenWrapper studySourceVessel = getSelection();
                    if (studySourceVessel != null)
                        addOrEditStudySourceVessel(false, studySourceVessel);
                }
            });
        }
        if (SessionManager.canDelete(SourceSpecimenWrapper.class, null)) {
            addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    SourceSpecimenWrapper studySourceVessel = getSelection();
                    if (studySourceVessel != null) {
                        if (!MessageDialog
                            .openConfirm(PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow().getShell(),
                                "Delete Study Source Vessel",
                                "Are you sure you want to delete this source vessel ?")) {
                            return;
                        }

                        selectedStudySourceVessels.remove(studySourceVessel);
                        setCollection(selectedStudySourceVessels);
                        deletedSourceVessels.add(studySourceVessel);
                        availableSourceVessels.add(studySourceVessel
                            .getSpecimenType());
                        notifyListeners();
                    }
                }
            });
        }
    }

    private void initSourceVessels() {
        // FIXME implemente this method
        // try {
        // availableSourceVessels = SourceVesselWrapper
        // .getAllSourceVessels(SessionManager.getAppService());
        // List<SourceSpecimenWrapper> studySourceVessels = study
        // .getStudySourceVesselCollection();
        // if (studySourceVessels != null) {
        // for (SourceSpecimenWrapper ssv : studySourceVessels) {
        // availableSourceVessels.remove(ssv.getSourceVessel());
        // }
        // }
        // } catch (final RemoteConnectFailureException exp) {
        // BioBankPlugin.openRemoteConnectErrorMessage(exp);
        // } catch (ApplicationException e) {
        // logger.error("initAllSourceVessel", e);
        // }
    }

    public List<SourceSpecimenWrapper> getAddedOrModifiedSourceSpecimens() {
        return addedOrModifiedSourceVessels;
    }

    public List<SourceSpecimenWrapper> getDeletedSourceSpecimens() {
        return deletedSourceVessels;
    }

    public void reload() {
        selectedStudySourceVessels = study.getSourceSpecimenCollection(true);
        if (selectedStudySourceVessels == null) {
            selectedStudySourceVessels = new ArrayList<SourceSpecimenWrapper>();
        }
        reloadCollection(selectedStudySourceVessels);
        addedOrModifiedSourceVessels = new ArrayList<SourceSpecimenWrapper>();
        deletedSourceVessels = new ArrayList<SourceSpecimenWrapper>();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((SourceSpecimenWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((SourceSpecimenWrapper) e2);
                    return super.compare(i1.name, i2.name);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

}
