package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.batchoperation.ceventattr.CeventAttrBatchOpPojoReader;
import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;
import edu.ualberta.med.biobank.model.Center;

public class CeventAttrImportForm extends ImportForm {
    private static final I18n i18n = I18nFactory.getI18n(CeventAttrImportForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.batchop.CeventAttrImportForm";

    @SuppressWarnings("nls")
    private static final String FORM_TITLE = i18n.tr("Collection Event Attribute Import");

    public CeventAttrImportForm() {
        super(FORM_TITLE);
    }

    @Override
    protected IBatchOpPojoReader<? extends IBatchOpInputPojo> getCsvPojoReader(Center center,
        String csvFilename, String[] csvHeaders) {
        if (CeventAttrBatchOpPojoReader.isHeaderValid(csvHeaders)) {
            return new CeventAttrBatchOpPojoReader(center, csvFilename);
        }
        return null;
    }

    @Override
    public void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        CeventAttrBatchOpViewForm.openForm(batchOpId, focusOnEditor);
    }

}
