package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;
import edu.ualberta.med.biobank.common.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.batchoperation.specimenPosition.SpecimenPositionBatchOpPojoReader;
import edu.ualberta.med.biobank.model.Center;

public class SpecimenPositionImportForm extends ImportForm {

    //private static Logger log = LoggerFactory.getLogger(SpecimenImportForm.class);

    private static final I18n i18n = I18nFactory.getI18n(SpecimenImportForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenPositionImportForm";

    @SuppressWarnings("nls")
    private static final String FORM_TITLE = i18n.tr("Specimen Position Import");

    public SpecimenPositionImportForm() {
        super(FORM_TITLE);
    }

    @Override
    protected void createFormContent() throws Exception {
        createCheckbox();
        super.createFormContent();
    }

    private void createCheckbox() {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
    }

    @Override
    protected IBatchOpPojoReader<? extends IBatchOpInputPojo> getCsvPojoReader(Center center,
                                                                               String csvFilename,
                                                                               String[] csvHeaders) {
        return new SpecimenPositionBatchOpPojoReader(center, csvFilename);
    }

    @Override
    public void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        SpecimenPositionBatchOpViewForm.openForm(batchOpId, focusOnEditor);
    }

}
