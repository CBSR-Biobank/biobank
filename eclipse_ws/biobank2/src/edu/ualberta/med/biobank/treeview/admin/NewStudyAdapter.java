package edu.ualberta.med.biobank.treeview.admin;

import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;

public class NewStudyAdapter extends AbstractNewAdapterBase {

    private Study study;

    public NewStudyAdapter(AbstractAdapterBase parent, Study study) {
        super(parent, study.getId(), null, null, false);
        this.study = study;
    }

    @Override
    protected String getLabelInternal() {
        return study == null ? "" : study.getNameShort(); //$NON-NLS-1$
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Messages.StudyAdapter_study_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Messages.StudyAdapter_study_label);
        addViewMenu(menu, Messages.StudyAdapter_study_label);
        addDeleteMenu(menu, Messages.StudyAdapter_study_label);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.StudyAdapter_delete_confirm_msg;
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
