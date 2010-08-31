package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.StudySourceVesselDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.StudySourceVesselInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudySourceVesselEntryInfoTable extends StudySourceVesselInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SampleStorageEntryInfoTable.class.getName());

    private List<SourceVesselWrapper> availableSourceVessels;

    private List<StudySourceVesselWrapper> selectedStudySourceVessels;

    private List<StudySourceVesselWrapper> addedOrModifiedSourceVessels;

    private List<StudySourceVesselWrapper> deletedSourceVessels;

    private StudyWrapper study;

    public StudySourceVesselEntryInfoTable(Composite parent,
        List<StudySourceVesselWrapper> collection) {
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
    public StudySourceVesselEntryInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null);
        this.study = study;
        initSourceVessels();
        selectedStudySourceVessels = study.getStudySourceVesselCollection();
        if (selectedStudySourceVessels == null) {
            selectedStudySourceVessels = new ArrayList<StudySourceVesselWrapper>();
        }
        setCollection(selectedStudySourceVessels);
        addedOrModifiedSourceVessels = new ArrayList<StudySourceVesselWrapper>();
        deletedSourceVessels = new ArrayList<StudySourceVesselWrapper>();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addStudySourceVessel() {
        StudySourceVesselWrapper newStudySourcevessel = new StudySourceVesselWrapper(
            SessionManager.getAppService());
        newStudySourcevessel.setStudy(study);
        addOrEditStudySourceVessel(true, newStudySourcevessel);
    }

    private void addOrEditStudySourceVessel(boolean add,
        StudySourceVesselWrapper studySourceVessel) {
        List<SourceVesselWrapper> dialogSourceVessels = availableSourceVessels;
        if (!add) {
            dialogSourceVessels.add(studySourceVessel.getSourceVessel());
        }
        StudySourceVesselDialog dlg = new StudySourceVesselDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            studySourceVessel, dialogSourceVessels);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedStudySourceVessels.add(studySourceVessel);
            }
            availableSourceVessels.remove(studySourceVessel.getSourceVessel());
            reloadCollection(selectedStudySourceVessels);
            addedOrModifiedSourceVessels.add(studySourceVessel);
            notifyListeners();
        }
    }

    private void addEditSupport() {
        if (SessionManager.canCreate(StudySourceVesselWrapper.class)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    addStudySourceVessel();
                }
            });
        }
        if (SessionManager.canUpdate(StudySourceVesselWrapper.class)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    StudySourceVesselWrapper studySourceVessel = getSelection();
                    if (studySourceVessel != null)
                        addOrEditStudySourceVessel(false, studySourceVessel);
                }
            });
        }
        if (SessionManager.canDelete(StudySourceVesselWrapper.class)) {
            addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    StudySourceVesselWrapper studySourceVessel = getSelection();
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
                            .getSourceVessel());
                        notifyListeners();
                    }
                }
            });
        }
    }

    private void initSourceVessels() {
        try {
            availableSourceVessels = SourceVesselWrapper
                .getAllSourceVessels(SessionManager.getAppService());
            List<StudySourceVesselWrapper> studySourceVessels = study
                .getStudySourceVesselCollection();
            if (studySourceVessels != null) {
                for (StudySourceVesselWrapper ssv : studySourceVessels) {
                    availableSourceVessels.remove(ssv.getSourceVessel());
                }
            }
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (ApplicationException e) {
            logger.error("initAllSourceVessel", e);
        }
    }

    public List<StudySourceVesselWrapper> getAddedOrModifiedStudySourceVessels() {
        return addedOrModifiedSourceVessels;
    }

    public List<StudySourceVesselWrapper> getDeletedStudySourceVessels() {
        return deletedSourceVessels;
    }

    public void reload() {
        selectedStudySourceVessels = study.getStudySourceVesselCollection();
        if (selectedStudySourceVessels == null) {
            selectedStudySourceVessels = new ArrayList<StudySourceVesselWrapper>();
        }
        reloadCollection(selectedStudySourceVessels);
        addedOrModifiedSourceVessels = new ArrayList<StudySourceVesselWrapper>();
        deletedSourceVessels = new ArrayList<StudySourceVesselWrapper>();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((StudySourceVesselWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((StudySourceVesselWrapper) e2);
                    return super.compare(i1.name, i2.name);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

}
