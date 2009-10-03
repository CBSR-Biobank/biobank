package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.PatientVisitInfoTable;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientViewForm";

    private PatientAdapter patientAdapter;

    private PatientWrapper patientWrapper;

    private Label siteLabel;

    private PatientVisitInfoTable visitsTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof PatientAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientAdapter = (PatientAdapter) adapter;
        patientWrapper = patientAdapter.getWrapper();
        retrievePatient();
        setPartName("Patient " + patientWrapper.getNumber());
    }

    private void retrievePatient() throws Exception {
        patientWrapper.reload();
    }

    @Override
    protected void createFormContent() {
        form.setText("Patient: " + patientWrapper.getNumber());
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addRefreshToolbarAction();
        createPatientSection();
        createPatientVisitSection();
        initEditButton(form.getBody(), patientAdapter);
        setValues();
    }

    private void createPatientSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = (Label) createWidget(client, Label.class, SWT.NONE, "Site");
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

    private void setValues() {
        FormUtils.setTextValue(siteLabel, patientWrapper.getStudy()
            .getWrappedObject().getSite().getName());
    }

    @Override
    protected void reload() throws Exception {
        setValues();
        retrievePatient();
        setPartName("Patient " + patientWrapper.getNumber());
        form.setText("Patient: " + patientWrapper.getNumber());
        visitsTable.setCollection(patientWrapper.getPatientVisitCollection());
    }

    @Override
    protected String getEntryFormId() {
        return PatientEntryForm.ID;
    }
}
