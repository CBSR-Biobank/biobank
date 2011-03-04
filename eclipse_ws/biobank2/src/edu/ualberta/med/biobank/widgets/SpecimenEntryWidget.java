package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperListenerAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable.ColumnsShown;
import edu.ualberta.med.biobank.widgets.infotables.entry.SpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenEntryWidget extends BiobankWidget {

    private CollectionEventWrapper shipment;

    private SpecimenInfoTable specTable;

    private BiobankText newPatientText;

    private boolean editable;

    private List<PatientAddListener> patientListeners;

    private Button addButton;

    private IObservableValue patientsAdded = new WritableValue(Boolean.FALSE,
        Boolean.class);

    public SpecimenEntryWidget(Composite parent, int style,
        CollectionEventWrapper ship, FormToolkit toolkit, boolean editable) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        this.shipment = ship;
        this.editable = editable;

        setLayout(new GridLayout(2, false));
        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        toolkit.paintBordersFor(this);

        if (editable) {
            Label label = toolkit.createLabel(this,
                "Enter patient number to add:");
            GridData gd = new GridData();
            gd.horizontalSpan = 2;
            label.setLayoutData(gd);
            newPatientText = new BiobankText(this, SWT.NONE, toolkit);
            newPatientText.addListener(SWT.DefaultSelection, new Listener() {
                @Override
                public void handleEvent(Event e) {
                    addPatient();
                    newPatientText.setFocus();
                    newPatientText.setText("");
                }
            });
            addButton = toolkit.createButton(this, "", SWT.PUSH);
            addButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
                .get(BiobankPlugin.IMG_ADD));
            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addPatient();
                }
            });
        }

        if (editable) {
            specTable = new SpecimenEntryInfoTable(this, null, ColumnsShown.ALL);
        } else {
            specTable = new SpecimenInfoTable(this, null, ColumnsShown.ALL, 20);
        }
        updateList();

        specTable.adaptToToolkit(toolkit, true);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        specTable.setLayoutData(gd);
        addDeleteSupport();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (patientListeners != null) {
            for (PatientAddListener listener : patientListeners) {
                listener.removeListener();
            }
        }
    }

    private void addPatient() {
        String patientNumber = newPatientText.getText().trim();
        if (!patientNumber.isEmpty()) {
            try {
                PatientWrapper patient = PatientWrapper.getPatient(
                    shipment.getAppService(), patientNumber,
                    SessionManager.getUser());
                if (patient == null) {
                    boolean create = BiobankPlugin.openConfirm(
                        "Patient not found",
                        "Do you want to create this patient ?");
                    if (create) {
                        patient = new PatientWrapper(
                            SessionManager.getAppService());
                        patient.setPnumber(patientNumber);
                        addPatientListener(patient);
                        PatientAdapter patientAdapter = new PatientAdapter(
                            null, patient);
                        // won't be able to edit it once created :
                        patientAdapter.setEditable(false);
                        patientAdapter.openEntryForm(true);

                    }
                } else {
                    addPatient(patient);
                }
            } catch (ApplicationException ae) {
                BiobankPlugin.openAsyncError("Error while looking up patient",
                    ae);
            }
        }
    }

    private void addPatientListener(PatientWrapper patient) {
        PatientAddListener listener = new PatientAddListener(patient);
        patient.addWrapperListener(listener);
        if (patientListeners == null) {
            patientListeners = new ArrayList<PatientAddListener>();
        }
        patientListeners.add(listener);
    }

    private void addPatient(PatientWrapper patient) {
        List<PatientWrapper> patients = shipment.getPatientCollection();
        if (patients != null && patients.contains(patient)) {
            BiobankPlugin.openAsyncError("Error",
                "Patient " + patient.getPnumber()
                    + " has already been added to this shipment");
            return;
        }
        boolean canAdd = false;
        try {
            canAdd = patient.canBeAddedToShipment(shipment);
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Can't add patient", e);
            return;
        }
        if (canAdd) {
            try {
                shipment.addPatients(Arrays.asList(patient));
            } catch (Exception e) {
                BiobankPlugin.openAsyncError("Cannot add patient", e);
                return;
            }
            // FIXME
            // specTable.setCollection(shipment.getSpecimenCollection(true));
            notifyListeners();
            patientsAdded.setValue(true);
        } else {
            BiobankPlugin.openAsyncError("Error",
                "Patient " + patient.getPnumber()
                    + " can't be added to this shipment. "
                    + "Patient study is not linked to this shipment clinic.");
        }
    }

    private void addDeleteSupport() {
        if (!editable)
            return;

        specTable.addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                SpecimenWrapper spec = specTable.getSelection();
                if (spec != null) {
                    if (!MessageDialog.openConfirm(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        "Delete Patient",
                        "Are you sure you want to remove patient \""
                            + spec.getCollectionEvent().getPatient()
                                .getPnumber() + "\" for this shipment ?")) {
                        return;
                    }
                    try {
                        // FIXME:
                        // shipment.checkCanRemovePatient(patient);
                        // shipment.removePatients(Arrays.asList(patient));
                    } catch (Exception e) {
                        BiobankPlugin
                            .openAsyncError("Cannot remove patient", e);
                        return;
                    }
                    updateList();
                    notifyListeners();
                }
            }
        });
    }

    public void updateList() {
        // FIXME
        // List<SpecimenWrapper> patients =
        // shipment.getSpecimenCollection(true);
        // if (patients != null)
        // specTable.setCollection(patients);
        // else
        // specTable.setCollection(new ArrayList<SpecimenWrapper>());
        // patientsAdded.setValue(patients != null && patients.size() > 0);
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        specTable.addClickListener(listener);
    }

    public class PatientAddListener extends WrapperListenerAdapter {
        private PatientWrapper patient;

        public PatientAddListener(PatientWrapper patient) {
            this.patient = patient;
        }

        public void removeListener() {
            patient.removeWrapperListener(this);
        }

        @Override
        public void inserted(WrapperEvent event) {
            if (!SpecimenEntryWidget.this.isDisposed()) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        addPatient(patient);
                    }
                });
            }
        }
    }

    public void addBinding(WidgetCreator dbc, final String message) {
        final ControlDecoration controlDecoration = createDecorator(addButton,
            message);
        WritableValue wv = new WritableValue(Boolean.FALSE, Boolean.class);
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    controlDecoration.show();
                    return ValidationStatus.error(message);
                } else {
                    controlDecoration.hide();
                    return Status.OK_STATUS;
                }
            }
        });
        dbc.bindValue(wv, patientsAdded, uvs, uvs);
    }
}
