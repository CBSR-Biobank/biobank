package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class PatientBatchOpViewForm extends BatchOpViewForm {

    private static final I18n i18n = I18nFactory.getI18n(BatchOpViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientBatchOpViewForm";

    @SuppressWarnings("nls")
    public static final String FORM_TITLE = i18n.tr("Patient Import");

    public PatientBatchOpViewForm() {
        super(FORM_TITLE);
    }

    public static void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        openForm(batchOpId, ID, focusOnEditor);
    }
}
