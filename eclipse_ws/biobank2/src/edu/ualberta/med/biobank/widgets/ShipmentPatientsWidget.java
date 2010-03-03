package edu.ualberta.med.biobank.widgets;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.views.ShipmentAdministrationView;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.PatientInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShipmentPatientsWidget extends BiobankWidget {

    private ShipmentWrapper shipment;

    private PatientInfoTable patientTable;

    private Text newPatientText;

    private SiteWrapper currentSite;

    private boolean editable;

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
            newPatientText = toolkit.createText(this, "");
            newPatientText.addListener(SWT.DefaultSelection, new Listener() {
                public void handleEvent(Event e) {
                    boolean newPatient = addPatient();
                    newPatientText.setFocus();
                    if (!newPatient) {
                        newPatientText.setText("");
                    }
                }
            });
            Button addButton = toolkit.createButton(this, "", SWT.PUSH);
            addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
                .get(BioBankPlugin.IMG_ADD));
            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addPatient();
                }
            });
        }

        patientTable = new PatientInfoTable(this, !editable, shipment
            .getPatientCollection());
        patientTable.adaptToToolkit(toolkit, true);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        patientTable.setLayoutData(gd);
        addDeleteSupport();
    }

    private boolean addPatient() {
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
                        ShipmentAdministrationView.currentInstance
                            .displayPatient(patient);
                        return true;
                    }
                } else {
                    addPatient(patient);
                }
            } catch (ApplicationException ae) {
                BioBankPlugin.openAsyncError("Error while looking up patient",
                    ae);
            }
        }
        return false;
    }

    private void addPatient(PatientWrapper patient) {
        List<PatientWrapper> patients = shipment.getPatientCollection();
        if (patients != null && patients.contains(patient)) {
            BioBankPlugin.openAsyncError("Error", "Patient "
                + patient.getPnumber()
                + " has already been added to this shipment");
            return;
        }
        shipment.addPatients(Arrays.asList(patient));
        patientTable.setCollection(shipment.getPatientCollection());
        notifyListeners();
    }

    private void addDeleteSupport() {
        if (!editable)
            return;

        patientTable.addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                PatientWrapper patient = patientTable.getSelection();
                if (!MessageDialog.openConfirm(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), "Delete Patient",
                    "Are you sure you want to remove patient \""
                        + patient.getPnumber() + "\" for this shipment ?")) {
                    return;
                }

                shipment.removePatients(Arrays.asList(patient));
                patientTable.setCollection(shipment.getPatientCollection());
                notifyListeners();
            }
        });
    }

    public void reloadList() {
        patientTable.setCollection(shipment.getPatientCollection());
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        patientTable.addDoubleClickListener(listener);
    }
}
