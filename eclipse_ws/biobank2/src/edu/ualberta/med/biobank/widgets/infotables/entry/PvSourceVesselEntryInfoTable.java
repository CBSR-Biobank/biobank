package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.dialogs.PvSourceVesselDialog;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.PvSourceVesselInfoTable;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PvSourceVesselEntryInfoTable extends PvSourceVesselInfoTable {

    private List<StudySourceVesselWrapper> studySourceVessels;

    private List<PvSourceVesselWrapper> selectedPvSourceVessels;

    private List<PvSourceVesselWrapper> addedPvSourceVessels;

    private List<PvSourceVesselWrapper> removedPvSourceVessels;

    private IObservableValue sourceVesselsAdded = new WritableValue(
        Boolean.FALSE, Boolean.class);

    private PatientVisitWrapper patientVisit;

    private List<SourceVesselWrapper> allSourceVessels;

    public PvSourceVesselEntryInfoTable(Composite parent,
        List<PvSourceVesselWrapper> collection) {
        super(parent, collection);
    }

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param style the style of control to construct
     * @param pvSourceVesselCollection the sample storage already selected and
     *            to be displayed in the table viewer (can be null).
     * @param toolkit The toolkit is responsible for creating SWT controls
     *            adapted to work in Eclipse forms. If widget is not used in a
     *            form this parameter should be null.
     */
    public PvSourceVesselEntryInfoTable(Composite parent,
        PatientVisitWrapper visit) {
        super(parent, null);
        this.patientVisit = visit;

        studySourceVessels = patientVisit.getPatient().getStudy()
            .getStudySourceVesselCollection(true);

        try {
            allSourceVessels = SourceVesselWrapper
                .getAllSourceVessels(SessionManager.getAppService());
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("Error retrievind source vessels", e);
        }

        selectedPvSourceVessels = patientVisit.getPvSourceVesselCollection();
        if (selectedPvSourceVessels == null) {
            selectedPvSourceVessels = new ArrayList<PvSourceVesselWrapper>();
        }
        setCollection(selectedPvSourceVessels);
        if (selectedPvSourceVessels.size() > 0) {
            sourceVesselsAdded.setValue(true);
        }
        addedPvSourceVessels = new ArrayList<PvSourceVesselWrapper>();
        removedPvSourceVessels = new ArrayList<PvSourceVesselWrapper>();

        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addPvSourceVessel() {
        addOrEditPvSourceVessel(true, null);
    }

    public void addBinding(WidgetCreator dbc) {
        final ControlDecoration controlDecoration = createDecorator(this,
            "Source vessels should be selected for this visit");
        WritableValue wv = new WritableValue(Boolean.FALSE, Boolean.class);
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    controlDecoration.show();
                    return ValidationStatus
                        .error("Source vessels should be added");
                } else {
                    controlDecoration.hide();
                    return Status.OK_STATUS;
                }
            }
        });
        dbc.bindValue(wv, sourceVesselsAdded, uvs, uvs);
    }

    private void addOrEditPvSourceVessel(boolean add,
        PvSourceVesselWrapper pvSourceVessel) {
        PvSourceVesselDialog dlg = new PvSourceVesselDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            pvSourceVessel, studySourceVessels, allSourceVessels, this);
        if (add) {
            dlg.setPatientVisit(patientVisit);
        }
        int res = dlg.open();
        if (!add && res == Dialog.OK) {
            reloadCollection(selectedPvSourceVessels);
            notifyListeners();
        }
    }

    /**
     * called from the dialog to add new source vessels into the table
     * 
     * @param pvSourceVessel
     */
    public void addPvSourceVessel(PvSourceVesselWrapper pvSourceVessel) {
        selectedPvSourceVessels.add(pvSourceVessel);
        addedPvSourceVessels.add(pvSourceVessel);
        sourceVesselsAdded.setValue(true);
        reloadCollection(selectedPvSourceVessels);
        notifyListeners();
    }

    private void addEditSupport() {
        if (SessionManager.canCreate(PvSourceVesselWrapper.class, null)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    addPvSourceVessel();
                }
            });
        }
        if (SessionManager.canUpdate(PvSourceVesselWrapper.class, null)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    PvSourceVesselWrapper svss = getSelection();
                    if (svss != null)
                        addOrEditPvSourceVessel(false, svss);
                }
            });
        }
        if (SessionManager.canDelete(PvSourceVesselWrapper.class, null)) {
            addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    PvSourceVesselWrapper svss = getSelection();
                    if (svss != null) {
                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), "Delete Aliquot Storage",
                            "Are you sure you want to delete source vessel \""
                                + svss.getSourceVessel().getName() + "\"?")) {
                            return;
                        }

                        selectedPvSourceVessels.remove(svss);
                        setCollection(selectedPvSourceVessels);
                        if (selectedPvSourceVessels.size() == 0) {
                            sourceVesselsAdded.setValue(false);
                        }
                        addedPvSourceVessels.remove(svss);
                        removedPvSourceVessels.add(svss);
                        notifyListeners();
                    }
                }
            });
        }
    }

    public void reload() {
        selectedPvSourceVessels = patientVisit.getPvSourceVesselCollection();
        if (selectedPvSourceVessels == null) {
            selectedPvSourceVessels = new ArrayList<PvSourceVesselWrapper>();
        }
        reloadCollection(selectedPvSourceVessels);
        addedPvSourceVessels = new ArrayList<PvSourceVesselWrapper>();
        removedPvSourceVessels = new ArrayList<PvSourceVesselWrapper>();
    }

    public List<PvSourceVesselWrapper> getAddedPvSourceVessels() {
        return addedPvSourceVessels;
    }

    public List<PvSourceVesselWrapper> getRemovedPvSourceVessels() {
        return removedPvSourceVessels;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((PvSourceVesselWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((PvSourceVesselWrapper) e2);
                    return super.compare(i1.name, i2.name);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

}
