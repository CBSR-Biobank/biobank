package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.patient.PatientInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientViewAction;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.NewCollectionEventInfoTable;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientViewForm"; //$NON-NLS-1$

    private BgcBaseText studyLabel;

    private BgcBaseText createdAtLabel;

    private BgcBaseText visitCountLabel;

    private BgcBaseText sourceSpecimenCountLabel;

    private BgcBaseText aliquotedSpecimenCountLabel;

    private NewCollectionEventInfoTable collectionEventTable;

    private PatientInfo patientInfo;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof PatientAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        updatePatientInfo();
        // FIXME log ?
        // SessionManager.logLookup(patientInfo.patient);
        setPartName(NLS.bind(Messages.PatientViewForm_title,
            patientInfo.patient.getPnumber()));
    }

    private void updatePatientInfo() throws Exception {
        patientInfo = SessionManager.getAppService().doAction(
            new PatientViewAction(
                ((PatientAdapter) adapter).getModelObject().patient.getId()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.PatientViewForm_title,
            patientInfo.patient.getPnumber()));
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
            Messages.patient_field_label_study);
        createdAtLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.PatientViewForm_label_createdAt);
        visitCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.PatientViewForm_label_totalVisits);
        sourceSpecimenCountLabel = createReadOnlyLabelledField(client,
            SWT.NONE, Messages.PatientViewForm_label_totalSourceSpecimens);
        aliquotedSpecimenCountLabel = createReadOnlyLabelledField(client,
            SWT.NONE, Messages.PatientViewForm_label_totalAliquotedSpecimens);
    }

    private void createCollectionEventSection() {
        Section section = createSection(Messages.PatientViewForm_visits_title);

        collectionEventTable = new NewCollectionEventInfoTable(section,
            patientInfo.cevents);
        section.setClient(collectionEventTable);
        collectionEventTable.adaptToToolkit(toolkit, true);
        collectionEventTable.addClickListener(collectionDoubleClickListener);
        collectionEventTable.createDefaultEditItem();
    }

    private void setValues() {
        setTextValue(studyLabel, patientInfo.patient.getStudy().getName());
        setTextValue(createdAtLabel,
            DateFormatter.formatAsDateTime(patientInfo.patient.getCreatedAt()));
        setTextValue(visitCountLabel, patientInfo.cevents.size());
        setTextValue(sourceSpecimenCountLabel, patientInfo.sourceSpecimenCount);
        setTextValue(aliquotedSpecimenCountLabel,
            patientInfo.aliquotedSpecimenCount);
    }

    @Override
    public void reload() throws Exception {
        updatePatientInfo();
        setValues();
        setPartName(NLS.bind(Messages.PatientViewForm_title,
            patientInfo.patient.getPnumber()));
        form.setText(NLS.bind(Messages.PatientViewForm_title,
            patientInfo.patient.getPnumber()));
        collectionEventTable.setCollection(patientInfo.cevents);
    }

}
