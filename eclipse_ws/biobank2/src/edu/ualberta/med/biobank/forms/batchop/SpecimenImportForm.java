package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;
import edu.ualberta.med.biobank.common.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.batchoperation.specimen.SpecimenPojoReaderFactory;
import edu.ualberta.med.biobank.model.Center;

public class SpecimenImportForm extends ImportForm {
    private static final I18n i18n = I18nFactory.getI18n(SpecimenImportForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenImportForm";

    @SuppressWarnings("nls")
    private static final String FORM_TITLE = i18n.tr("Specimen Import");

    public SpecimenImportForm() {
        super(FORM_TITLE);
    }

    @Override
    protected IBatchOpPojoReader<? extends IBatchOpInputPojo> getCsvPojoReader(Center center,
        String csvFilename, String[] csvHeaders) {
        return SpecimenPojoReaderFactory.createPojoReader(center, csvFilename, csvHeaders);
    }

    @Override
    public void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        SpecimenBatchOpViewForm.openForm(batchOpId, focusOnEditor);
    }

}