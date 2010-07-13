package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.widgets.infotables.ClinicVisitInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientMergeForm extends BiobankEntryForm {

    @SuppressWarnings("unused")
    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientMergeForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientMergeForm";

    public static final String MSG_PATIENT_NOT_VALID = "Select a second patient";

    private PatientAdapter patient1Adapter;

    private PatientWrapper patient2Wrapper;

    BiobankText study2;

    ClinicVisitInfoTable patient2Visits;

    private BiobankText pnumber2;

    private BiobankText pnumber1;

    private BiobankText study1;

    IObservableValue patientNotNull;

    private AbstractInfoTableWidget<PatientVisitWrapper> patient1Visits;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patient1Adapter = (PatientAdapter) adapter;
        String tabName = "Merging Patient "
            + patient1Adapter.getWrapper().getPnumber();
        setPartName(tabName);
        patientNotNull = new WritableValue(Boolean.FALSE, Boolean.class);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), patientNotNull, MSG_PATIENT_NOT_VALID);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Information");
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_PATIENT));
        createPatientSection();
    }

    private void createPatientSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout toplayout = new GridLayout(2, true);
        toplayout.horizontalSpacing = 10;
        client.setLayout(toplayout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite patientArea1 = toolkit.createComposite(client);
        GridLayout patient1Layout = new GridLayout(2, false);
        patient1Layout.horizontalSpacing = 10;
        patientArea1.setLayout(patient1Layout);
        GridData patient1Data = new GridData();
        patient1Data.grabExcessHorizontalSpace = true;
        patient1Data.horizontalAlignment = SWT.FILL;
        patient1Data.verticalAlignment = SWT.FILL;
        patientArea1.setLayoutData(patient1Data);

        Composite patientArea2 = toolkit.createComposite(client);
        GridLayout patient2Layout = new GridLayout(2, false);
        patient2Layout.horizontalSpacing = 10;
        patientArea2.setLayout(patient2Layout);
        GridData patient2Data = new GridData();
        patient2Data.grabExcessHorizontalSpace = true;
        patient2Data.horizontalAlignment = SWT.FILL;
        patient2Data.verticalAlignment = SWT.FILL;
        patientArea2.setLayoutData(patient2Data);
        toolkit.paintBordersFor(client);

        pnumber1 = createReadOnlyLabelledField(patientArea1, SWT.NONE,
            "Patient Number");
        pnumber1.setText(patient1Adapter.getWrapper().getPnumber());

        pnumber2 = (BiobankText) createLabelledWidget(patientArea2,
            BiobankText.class, SWT.NONE, "Patient Number");
        pnumber2.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                patientNotNull.setValue(Boolean.FALSE);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR)
                    populateFields(pnumber2.getText());
            }
        });
        pnumber2.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.TAB)
                    populateFields(pnumber2.getText());
            }

        });

        setFirstControl(pnumber2);

        StudyWrapper selectedStudy = patient1Adapter.getWrapper().getStudy();
        study1 = createReadOnlyLabelledField(patientArea1, SWT.NONE, "Study");
        study1.setText(selectedStudy.getNameShort());

        study2 = createReadOnlyLabelledField(patientArea2, SWT.NONE, "Study");

        patient1Visits = new ClinicVisitInfoTable(patientArea1, patient1Adapter
            .getWrapper().getPatientVisitCollection(true, true));
        GridData gd1 = new GridData();
        gd1.horizontalSpan = 2;
        gd1.grabExcessHorizontalSpace = true;
        gd1.horizontalAlignment = SWT.FILL;
        patient1Visits.setLayoutData(gd1);

        patient2Visits = new ClinicVisitInfoTable(patientArea2,
            new ArrayList<PatientVisitWrapper>());
        GridData gd2 = new GridData();
        gd2.horizontalSpan = 2;
        gd2.grabExcessHorizontalSpace = true;
        gd2.horizontalAlignment = SWT.FILL;
        patient2Visits.setLayoutData(gd2);
    }

    protected void populateFields(String pnumber) {
        try {
            patient2Wrapper = PatientWrapper.getPatientInSite(SessionManager
                .getAppService(), pnumber, SessionManager.getInstance()
                .getCurrentSite());
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("Error retrieving patient", e);
            return;
        }
        if (patient2Wrapper == null) {
            BioBankPlugin.openAsyncError("Invalid Patient Number",
                "Cannot find a patient with that pnumber");
            return;
        }

        study2.setText(patient2Wrapper.getStudy().getNameShort());

        if (!patient2Wrapper.getStudy().equals(
            patient1Adapter.getWrapper().getStudy()))
            BioBankPlugin.openAsyncError("Invalid Patient Number",
                "Patients from different studies cannot be merged");
        else {
            patient2Visits.setCollection(patient2Wrapper
                .getPatientVisitCollection());
            patient2Visits.layout();
            patientNotNull.setValue(Boolean.TRUE);
        }
    }

    private void merge() {
        try {
            patient1Adapter.getWrapper().addPatientVisits(
                patient2Wrapper.getPatientVisitCollection());
            patient2Wrapper
                .setPatientVisitCollection(new ArrayList<PatientVisitWrapper>());
            List<ShipmentWrapper> shipments = patient2Wrapper
                .getShipmentCollection();
            for (ShipmentWrapper shipment : shipments) {
                List<PatientWrapper> patients = shipment.getPatientCollection();
                for (PatientWrapper p : patients)
                    if (p.equals(patient2Wrapper)) {
                        shipment.removePatients(Arrays
                            .asList(new PatientWrapper[] { patient2Wrapper }));
                        shipment.addPatients(Arrays
                            .asList(new PatientWrapper[] { patient1Adapter
                                .getWrapper() }));
                        break;
                    }
                shipment.persist();
            }
            patient1Adapter.getWrapper().persist();
            patient2Wrapper.reload();

            PatientAdapter p = (PatientAdapter) SessionManager
                .searchNode(patient2Wrapper);
            p.getParent().removeChild(p);
            SessionManager.getCurrentAdapterViewWithTree().getTreeViewer()
                .refresh();

            patient2Wrapper.delete();
            this.closeEntryOpenView(false, true);
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Merge failed.", e);
        }

    }

    @Override
    protected void saveForm() throws Exception {
        if (patient2Wrapper != null) {
            if (BioBankPlugin.openConfirm(
                "Confirm Merge",
                "Are you sure you want to merge patient "
                    + patient2Wrapper.getPnumber() + " into patient "
                    + patient1Adapter.getWrapper().getPnumber()
                    + "? All patient visits will be transferred.")) {
                merge();
            }
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        pnumber1.setText(patient1Adapter.getWrapper().getPnumber());
        study1.setText(patient1Adapter.getWrapper().getStudy().getNameShort());
        patient1Visits.setCollection(patient1Adapter.getWrapper()
            .getPatientVisitCollection(true, true));
        pnumber2.setText("");
        study2.setText("");
        patient2Visits.setCollection(new ArrayList<PatientVisitWrapper>());
        patient2Wrapper = null;
    }

    @Override
    protected String getOkMessage() {
        return "Patient " + patient2Wrapper.getPnumber()
            + " will be merged into patient "
            + patient1Adapter.getWrapper().getPnumber();
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }
}
