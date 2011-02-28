package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.CollectionEventInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientViewForm";

    private PatientAdapter patientAdapter;

    private PatientWrapper patient;

    private BiobankText studyLabel;

    private BiobankText visitCountLabel;

    private BiobankText sourceSpecimenCountLabel;

    private BiobankText aliquotedSpecimenCountLabel;

    private CollectionEventInfoTable collectionEventTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof PatientAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientAdapter = (PatientAdapter) adapter;
        patient = patientAdapter.getWrapper();
        retrievePatient();
        patient.logLookup(null);
        setPartName(Messages.getString("PatientViewForm.title",
            patient.getPnumber()));
    }

    private void retrievePatient() throws Exception {
        patient.reload();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("PatientViewForm.title",
            patient.getPnumber()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createPatientSection();
        createPatientVisitSection();
        setValues();
    }

    private void createPatientSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("patient.field.label.study"));
        visitCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("PatientViewForm.label.totalVisits"));
        sourceSpecimenCountLabel = createReadOnlyLabelledField(client,
            SWT.NONE,
            Messages.getString("PatientViewForm.label.totalSourceSpecimens"));
        aliquotedSpecimenCountLabel = createReadOnlyLabelledField(client,
            SWT.NONE,
            Messages.getString("PatientViewForm.label.totalAliquotedSpecimens"));
    }

    private void createPatientVisitSection() {
        Section section = createSection(Messages
            .getString("PatientViewForm.visits.title"));

        collectionEventTable = new CollectionEventInfoTable(section,
            patient.getCollectionEventCollection(true));
        section.setClient(collectionEventTable);
        collectionEventTable.adaptToToolkit(toolkit, true);
        collectionEventTable.addClickListener(collectionDoubleClickListener);
    }

    private void setValues() throws BiobankException, ApplicationException {
        setTextValue(studyLabel, patient.getStudy().getName());
        setTextValue(visitCountLabel,
            (patient.getProcessingEventCollection() == null) ? 0 : patient
                .getProcessingEventCollection().size());
        setTextValue(sourceSpecimenCountLabel, patient.getSourceSpecimenCount());
        setTextValue(aliquotedSpecimenCountLabel,
            patient.getAliquotedSpecimenCount());
    }

    @Override
    public void reload() throws Exception {
        setValues();
        retrievePatient();
        setPartName("Patient " + patient.getPnumber());
        form.setText("Patient: " + patient.getPnumber());
        collectionEventTable.setCollection(patient
            .getCollectionEventCollection(true));
    }

}
