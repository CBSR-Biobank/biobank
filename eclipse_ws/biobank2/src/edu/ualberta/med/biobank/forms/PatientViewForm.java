package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.widgets.BiobankCollectionTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientViewForm";

    private PatientAdapter patientAdapter;

    private Patient patient;

    // private Label patientNumberLabel;
    private BiobankCollectionTable visitsTable;

    @Override
    public void init() {
        Assert.isTrue(adapter instanceof PatientAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientAdapter = (PatientAdapter) adapter;
        retrievePatient();
        setPartName("Patient " + patient.getNumber());
    }

    private void retrievePatient() {
        List<Patient> result;
        Patient searchPatient = new Patient();
        searchPatient.setId(patientAdapter.getPatient().getId());
        try {
            result = appService.search(Patient.class, searchPatient);
            Assert.isTrue(result.size() == 1);
            patient = result.get(0);
            patientAdapter.setPatient(patient);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Patient: " + patient.getNumber());
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addRefreshToolbarAction();
        // createPatientSection();
        createPatientVisitSection();
    }

    // private void createPatientSection() {
    // Composite client = toolkit.createComposite(form.getBody());
    // GridLayout layout = new GridLayout(2, false);
    // layout.horizontalSpacing = 10;
    // client.setLayout(layout);
    // client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    // toolkit.paintBordersFor(client);
    //      
    // patientNumberLabel = (Label)createWidget(client, Text.class, SWT.NONE,
    // "Patient Number");
    // FormUtils.setTextValue(patientNumberLabel, patient.getNumber());
    // }

    private void createPatientVisitSection() {
        Section section = createSection("Patient Visits");

        String[] headings = new String[] { "Visit Number", "Num Samples" };
        visitsTable = new BiobankCollectionTable(section, SWT.NONE, headings,
            getPatientVisitAdapters());
        section.setClient(visitsTable);
        visitsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(visitsTable);

        visitsTable.getTableViewer().addDoubleClickListener(
            FormUtils.getBiobankCollectionDoubleClickListener());
    }

    private PatientVisitAdapter[] getPatientVisitAdapters() {
        // hack required here because xxx.getXxxxCollection().toArray(new
        // Xxx[0])
        // returns Object[].
        int count = 0;
        Collection<PatientVisit> visits = patient.getPatientVisitCollection();
        PatientVisitAdapter[] arr = new PatientVisitAdapter[visits.size()];
        for (PatientVisit visit : visits) {
            arr[count] = new PatientVisitAdapter(patientAdapter, visit);
            ++count;
        }
        return arr;
    }

    @Override
    protected void reload() {
        retrievePatient();
        setPartName("Patient " + patient.getNumber());
        form.setText("Patient: " + patient.getNumber());
        // FormUtils.setTextValue(patientNumberLabel, patient.getNumber());
        visitsTable.getTableViewer().setInput(getPatientVisitAdapters());
    }
}
