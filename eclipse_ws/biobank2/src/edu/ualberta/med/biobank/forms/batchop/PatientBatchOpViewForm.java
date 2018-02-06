package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpGetResult;
import edu.ualberta.med.biobank.common.action.batchoperation.patient.PatientBatchOpGetAction;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.widgets.infotables.PatientTableSimple;

public class PatientBatchOpViewForm extends BatchOpViewForm {

    private static final I18n i18n = I18nFactory.getI18n(BatchOpViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.batchop.PatientBatchOpViewForm";

    @SuppressWarnings("nls")
    public static final String FORM_TITLE = i18n.tr("Patient Import");

    @SuppressWarnings("nls")
    public static final String CONTENTS_TITLE = i18n.tr("Imported Patients");

    private PatientTableSimple patientTable;

    private BatchOpGetResult<Patient> result;

    public PatientBatchOpViewForm() {
        super(FORM_TITLE);
    }

    @Override
    protected void init() throws Exception {
        setBatchId(((BatchOpViewFormInput) getEditorInput()).getBatchOpId());
        result = SessionManager.getAppService().doAction(new PatientBatchOpGetAction(getBatchId()));

        setExecutedBy(result.getExecutedBy());
        setTimeExecuted(result.getTimeExecuted());
        setFileMetaData(result.getInput());
    }

    @Override
    protected void createFormContents() {
        Composite client = createSectionWithClient(CONTENTS_TITLE);
        Section section = (Section) client.getParent();
        section.setExpanded(true);

        patientTable = new PatientTableSimple(client, result.getModelObjects());
        patientTable.adaptToToolkit(toolkit, true);
        patientTable.layout(true, true);

        section.layout(true, true);
    }

    @Override
    public void setValues() throws Exception {
        super.setValues();
        patientTable.setCollection(result.getModelObjects());
        patientTable.reload();
    }

    public static void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        openForm(batchOpId, ID, focusOnEditor);
    }
}
