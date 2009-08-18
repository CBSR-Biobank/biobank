package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.PatientVisitInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientViewForm";

    private PatientAdapter patientAdapter;

    private Patient patient;

    private PatientVisitInfoTable visitsTable;

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
        createPatientVisitSection();
    }

    private void createPatientVisitSection() {
        Section section = createSection("Patient Visits");

        visitsTable = new PatientVisitInfoTable(section, patient
            .getPatientVisitCollection());
        section.setClient(visitsTable);
        visitsTable.adaptToToolkit(toolkit, true);
        visitsTable.getTableViewer().addDoubleClickListener(
            FormUtils.getBiobankCollectionDoubleClickListener());
    }

    @Override
    protected void reload() {
        retrievePatient();
        setPartName("Patient " + patient.getNumber());
        form.setText("Patient: " + patient.getNumber());
        // FormUtils.setTextValue(patientNumberLabel, patient.getNumber());
        visitsTable.setCollection(patient.getPatientVisitCollection());
    }
}
