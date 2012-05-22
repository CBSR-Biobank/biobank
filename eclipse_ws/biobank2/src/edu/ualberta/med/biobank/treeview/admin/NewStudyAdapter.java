package edu.ualberta.med.biobank.treeview.admin;

import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;

public class NewStudyAdapter extends AbstractNewAdapterBase {
    private static final I18n i18n = I18nFactory.getI18n(NewStudyAdapter.class);

    private final Study study;

    public NewStudyAdapter(AbstractAdapterBase parent, Study study) {
        super(parent, study.getId(), null, null, false);
        this.study = study;
    }

    @Override
    protected String getLabelInternal() {
        return study == null ? StringUtil.EMPTY_STRING : study.getNameShort();
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Study.NAME.singular().toString());
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Study.NAME.singular().toString());
        addViewMenu(menu, Study.NAME.singular().toString());
        addDeleteMenu(menu, Study.NAME.singular().toString());
    }

    @SuppressWarnings("nls")
    @Override
    protected String getConfirmDeleteMessage() {
        return i18n.tr("Are you sure you want to delete this study?");
    }

    @Override
    protected AbstractAdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AbstractAdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public String getViewFormId() {
        return StudyViewForm.ID;
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        return null;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof NewStudyAdapter)
            return study.getNameShort().compareTo(
                ((NewStudyAdapter) o).study.getNameShort());
        return 0;
    }

    @Override
    public void setValue(Object value) {
    }

    @Override
    protected void runDelete() throws Exception {
    }
}
