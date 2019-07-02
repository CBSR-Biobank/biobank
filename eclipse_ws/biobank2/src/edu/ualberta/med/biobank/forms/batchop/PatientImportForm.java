package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;
import edu.ualberta.med.biobank.common.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.batchoperation.patient.PatientBatchOpPojoReader;
import edu.ualberta.med.biobank.model.Center;

public class PatientImportForm extends ImportForm {
    private static final I18n i18n = I18nFactory.getI18n(PatientImportForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.batchop.PatientImportForm";

    @SuppressWarnings("nls")
    private static final String FORM_TITLE = i18n.tr("Patient Import");

    public PatientImportForm() {
        super(FORM_TITLE);
    }

    @Override
    protected IBatchOpPojoReader<? extends IBatchOpInputPojo> getCsvPojoReader(Center center,
        String csvFilename, String[] csvHeaders) {
        if (PatientBatchOpPojoReader.isHeaderValid(csvHeaders)) {
            return new PatientBatchOpPojoReader(center, csvFilename, false);
        }
        return null;
    }

    @Override
    public void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        PatientBatchOpViewForm.openForm(batchOpId, focusOnEditor);
    }

}
