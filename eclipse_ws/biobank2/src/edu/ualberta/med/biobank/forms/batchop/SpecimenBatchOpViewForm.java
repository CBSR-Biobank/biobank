package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class SpecimenBatchOpViewForm extends BatchOpViewForm {

    private static final I18n i18n = I18nFactory.getI18n(BatchOpViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenBatchOpViewForm";

    @SuppressWarnings("nls")
    public static final String FORM_TITLE = i18n.tr("Specimen Import");

    public SpecimenBatchOpViewForm() {
        super(FORM_TITLE);
    }

    public static void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        openForm(batchOpId, ID, focusOnEditor);
    }
}
