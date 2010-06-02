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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperListenerAdapter;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.PatientInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.PatientEntryInfoTable;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentPatientsWidget extends BiobankWidget {

    private ShipmentWrapper shipment;

    private PatientInfoTable patientTable;

    private BiobankText newPatientText;

    private SiteWrapper currentSite;

    private boolean editable;

    private List<PatientAddListener> patientListeners;

    private Button addButton;

    private IObservableValue patientsAdded = new WritableValue(Boolean.FALSE,
        Boolean.class);

    public ShipmentPatientsWidget(Composite parent, int style,
        ShipmentWrapper ship, final SiteWrapper site, FormToolkit toolkit,
        boolean editable) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        this.shipment = ship;
        this.currentSite = site;
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
                public void handleEvent(Event e) {
                    addPatient();
                    newPatientText.setFocus();
                    newPatientText.setText("");
                }
            });
            addButton = toolkit.createButton(this, "", SWT.PUSH);
            addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
                .get(BioBankPlugin.IMG_ADD));
            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addPatient();
                }
            });
        }

        if (editable) {
            patientTable = new PatientEntryInfoTable(this, null);
        } else {
            patientTable = new PatientInfoTable(this, null);
        }
        updateList();

        patientTable.adaptToToolkit(toolkit, true);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        patientTable.setLayoutData(gd);
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
                PatientWrapper patient = PatientWrapper.getPatientInSite(
                    shipment.getAppService(), patientNumber, currentSite);
                if (patient == null) {
                    boolean create = BioBankPlugin.openConfirm(
                        "Patient not found",
                        "Do you want to create this patient ?");
                    if (create) {
                        patient = new PatientWrapper(SessionManager
                            .getAppService());
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
                BioBankPlugin.openAsyncError("Error while looking up patient",
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
            BioBankPlugin.openAsyncError("Error", "Patient "
                + patient.getPnumber()
                + " has already been added to this shipment");
            return;
        }
        boolean canAdd = false;
        try {
            canAdd = patient.canBeAddedToShipment(shipment);
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Can't add patient", e);
            return;
        }
        if (canAdd) {
            shipment.addPatients(Arrays.asList(patient));
            patientTable.setCollection(shipment.getPatientCollection());
            notifyListeners();
            patientsAdded.setValue(true);
        } else {
            BioBankPlugin.openAsyncError("Error", "Patient "
                + patient.getPnumber() + " can't be added to this shipment. "
                + "Patient study is not linked to this shipment clinic.");
        }
    }

    private void addDeleteSupport() {
        if (!editable)
            return;

        patientTable.addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                PatientWrapper patient = patientTable.getSelection();
                if (patient != null) {
                    if (!MessageDialog.openConfirm(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        "Delete Patient",
                        "Are you sure you want to remove patient \""
                            + patient.getPnumber() + "\" for this shipment ?")) {
                        return;
                    }
                    try {
                        shipment.checkCanRemovePatient(patient);
                    } catch (Exception e) {
                        BioBankPlugin
                            .openAsyncError("Cannot remove patient", e);
                        return;
                    }
                    shipment.removePatients(Arrays.asList(patient));
                    updateList();
                    notifyListeners();
                }
            }
        });
    }

    public void updateList() {
        List<PatientWrapper> patients = shipment.getPatientCollection();
        patientTable.setCollection(patients);
        patientsAdded.setValue(patients != null && patients.size() > 0);
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        patientTable.addDoubleClickListener(listener);
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
            if (!ShipmentPatientsWidget.this.isDisposed()) {
                addPatient(patient);
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
