package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class PatientMergeForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientMergeForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientMergeForm";

    public static final String MSG_MERGE_PATIENTS_OK = "Creating a new patient record.";

    public static final String MSG_TWO_NOT_SELECTED = "A merge requires two patients";

    public static final String MSG_NO_STUDY_MATCH = "Patients must belong to the same study";

    private PatientAdapter patient1Adapter;

    private PatientWrapper patient2Wrapper;

    private SiteWrapper siteWrapper;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patient1Adapter = (PatientAdapter) adapter;
        String tabName = "Merging Patient "
            + patient1Adapter.getWrapper().getPnumber();
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_PATIENT));

        createPatientSection();

        if (patient1Adapter.getWrapper().isNew()) {
            setDirty(true);
        }
    }

    private void createPatientSection() throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        BiobankText labelSite = createReadOnlyLabelledField(client, SWT.NONE,
            "Site");
        siteWrapper = SessionManager.getInstance().getCurrentSite();
        labelSite.setText(siteWrapper.getName());
        siteWrapper.reload();
        StudyWrapper selectedStudy = patient1Adapter.getWrapper().getStudy();
        BiobankText study = createReadOnlyLabelledField(client, SWT.NONE,
            "Study");
        study.setText(selectedStudy.getName());

    }

    @Override
    protected String getOkMessage() {
        return MSG_MERGE_PATIENTS_OK;
    }

    @Override
    protected void saveForm() throws Exception {
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
    }
}
