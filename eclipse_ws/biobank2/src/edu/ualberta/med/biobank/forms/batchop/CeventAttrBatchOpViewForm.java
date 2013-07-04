package edu.ualberta.med.biobank.forms.batchop;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpGetResult;
import edu.ualberta.med.biobank.common.action.batchoperation.ceventattr.CeventAttrBatchOpGetAction;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.widgets.infotables.EventAttrTable;

public class CeventAttrBatchOpViewForm extends BatchOpViewForm {

    private static final I18n i18n = I18nFactory.getI18n(BatchOpViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.batchop.CeventAttrBatchOpViewForm";

    @SuppressWarnings("nls")
    public static final String FORM_TITLE = i18n.tr("Collection Event Attribute Import");

    @SuppressWarnings("nls")
    public static final String CONTENTS_TITLE = i18n.tr("Imported Collection Event Attributes");

    private EventAttrTable attrsTable;

    private BatchOpGetResult<EventAttr> result;

    public CeventAttrBatchOpViewForm() {
        super(FORM_TITLE);
    }

    @Override
    protected void init() throws Exception {
        setBatchId(((SpecimenBatchOpViewFormInput) getEditorInput()).getBatchOpId());
        result = SessionManager.getAppService().doAction(
            new CeventAttrBatchOpGetAction(getBatchId()));

        setExecutedBy(result.getExecutedBy());
        setTimeExecuted(result.getTimeExecuted());
        setFileMetaData(result.getInput());
    }

    @Override
    protected void createFormContents() {
        Composite client = createSectionWithClient(CONTENTS_TITLE);
        Section section = (Section) client.getParent();
        section.setExpanded(true);

        attrsTable = new EventAttrTable(client, result.getModelObjects());
        attrsTable.adaptToToolkit(toolkit, true);
        attrsTable.layout(true, true);

        section.layout(true, true);
    }

    @Override
    public void setValues() throws Exception {
        super.setValues();
        attrsTable.setCollection(result.getModelObjects());
        attrsTable.reload();
    }

    public static void openForm(Integer batchOpId, boolean focusOnEditor) throws PartInitException {
        openForm(batchOpId, ID, focusOnEditor);
    }
}
