package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.SpecimenPositionBatchOpGetAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.SpecimenPositionBatchOpGetResult;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenPositionTable;

public class SpecimenPositionBatchOpViewForm extends BatchOpViewForm {

    private static final I18n i18n = I18nFactory.getI18n(BatchOpViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenPositionBatchOpViewForm";

    @SuppressWarnings("nls")
    public static final String FORM_TITLE = i18n.tr("Specimen Position Import");

    @SuppressWarnings("nls")
    public static final String CONTENTS_TITLE = i18n.tr("Imported Specimens Positions");

    private SpecimenPositionTable specimenTable;

    private SpecimenPositionBatchOpGetResult result;

    public SpecimenPositionBatchOpViewForm() {
        super(FORM_TITLE);
    }

    @Override
    protected void init() throws Exception {
        setBatchId(((SpecimenBatchOpViewFormInput) getEditorInput()).getBatchOpId());
        result = SessionManager.getAppService().doAction(new SpecimenPositionBatchOpGetAction(getBatchId()));

        setExecutedBy(result.getExecutedBy());
        setTimeExecuted(result.getTimeExecuted());
        setFileMetaData(result.getInput());
    }

    @Override
    protected void createFormContents() {
        Composite client = createSectionWithClient(CONTENTS_TITLE);
        Section section = (Section) client.getParent();
        section.setExpanded(true);

        specimenTable = new SpecimenPositionTable(client, result.getSpecimenData());
        specimenTable.layout(true, true);

        section.layout(true, true);
    }

    @Override
    public void setValues() throws Exception {
        super.setValues();
        specimenTable.setCollection(result.getSpecimenData());
        specimenTable.reload();
    }

    public static void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        openForm(batchOpId, ID, focusOnEditor);
    }
}
