package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CollectionEventInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientViewForm"; //$NON-NLS-1$

    private PatientAdapter patientAdapter;

    private PatientWrapper patient;

    private BgcBaseText studyLabel;

    private BgcBaseText createdAtLabel;

    private BgcBaseText visitCountLabel;

    private BgcBaseText sourceSpecimenCountLabel;

    private BgcBaseText aliquotedSpecimenCountLabel;

    private CollectionEventInfoTable collectionEventTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof PatientAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        patientAdapter = (PatientAdapter) adapter;
        patient = patientAdapter.getWrapper();
        retrievePatient();
        SessionManager.logLookup(patient);
        setPartName(Messages.getString("PatientViewForm.title", //$NON-NLS-1$
            patient.getPnumber()));
    }

    private void retrievePatient() throws Exception {
        patient.reload();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("PatientViewForm.title", //$NON-NLS-1$
            patient.getPnumber()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createPatientSection();
        createCollectionEventSection();
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
            Messages.getString("patient.field.label.study")); //$NON-NLS-1$
        createdAtLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("PatientViewForm.label.createdAt")); //$NON-NLS-1$
        visitCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("PatientViewForm.label.totalVisits")); //$NON-NLS-1$
        sourceSpecimenCountLabel = createReadOnlyLabelledField(client,
            SWT.NONE,
            Messages.getString("PatientViewForm.label.totalSourceSpecimens")); //$NON-NLS-1$
        aliquotedSpecimenCountLabel = createReadOnlyLabelledField(client,
            SWT.NONE,
            Messages.getString("PatientViewForm.label.totalAliquotedSpecimens")); //$NON-NLS-1$
    }

    private void createCollectionEventSection() {
        Section section = createSection(Messages
            .getString("PatientViewForm.visits.title")); //$NON-NLS-1$

        collectionEventTable = new CollectionEventInfoTable(section,
            patient.getCollectionEventCollection(true));
        section.setClient(collectionEventTable);
        collectionEventTable.adaptToToolkit(toolkit, true);
        collectionEventTable.addClickListener(collectionDoubleClickListener);
    }

    private void setValues() throws ApplicationException, BiobankException {
        setTextValue(studyLabel, patient.getStudy().getName());
        setTextValue(createdAtLabel,
            DateFormatter.formatAsDateTime(patient.getCreatedAt()));
        setTextValue(visitCountLabel, patient.getCollectionEventCount(true)
            .toString());
        setTextValue(sourceSpecimenCountLabel,
            patient.getSourceSpecimenCount(true));
        setTextValue(aliquotedSpecimenCountLabel,
            patient.getAliquotedSpecimenCount(true));
    }

    @Override
    public void reload() throws Exception {
        setValues();
        retrievePatient();
        setPartName(Messages.getString("PatientViewForm.title", //$NON-NLS-1$
            patient.getPnumber()));
        form.setText(Messages.getString("PatientViewForm.title", //$NON-NLS-1$
            patient.getPnumber()));
        collectionEventTable.setCollection(patient
            .getCollectionEventCollection(true));
    }

}
