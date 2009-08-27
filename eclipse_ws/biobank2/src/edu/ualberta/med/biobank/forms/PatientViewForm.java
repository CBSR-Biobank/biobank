package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.PatientVisitInfoTable;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientViewForm";

    private PatientAdapter patientAdapter;

    private PatientWrapper patientWrapper;

    private PatientVisitInfoTable visitsTable;

    @Override
    public void init() {
        Assert.isTrue(adapter instanceof PatientAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientAdapter = (PatientAdapter) adapter;
        patientWrapper = patientAdapter.getWrapper();
        retrievePatient();
        setPartName("Patient " + patientWrapper.getNumber());
    }

    private void retrievePatient() {
        try {
            patientWrapper.reload();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while retrieving patient "
                    + patientAdapter.getWrapper().getNumber(), e);
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Patient: " + patientWrapper.getNumber());
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addRefreshToolbarAction();
        createPatientVisitSection();
    }

    private void createPatientVisitSection() {
        Section section = createSection("Patient Visits");

        visitsTable = new PatientVisitInfoTable(section, patientWrapper
            .getPatientVisitCollection());
        section.setClient(visitsTable);
        visitsTable.adaptToToolkit(toolkit, true);
        visitsTable.getTableViewer().addDoubleClickListener(
            FormUtils.getBiobankCollectionDoubleClickListener());
    }

    @Override
    protected void reload() {
        retrievePatient();
        setPartName("Patient " + patientWrapper.getNumber());
        form.setText("Patient: " + patientWrapper.getNumber());
        visitsTable.setCollection(patientWrapper.getPatientVisitCollection());
    }
}
