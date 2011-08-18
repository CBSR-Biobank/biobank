package edu.ualberta.med.biobank.treeview.admin;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class StudyAdapter extends AdapterBase {

    public StudyAdapter(AdapterBase parent, StudyWrapper studyWrapper) {
        super(parent, studyWrapper);
        setEditable(parent instanceof StudyMasterGroup || parent == null);
    }

    @Override
    protected String getLabelInternal() {
        StudyWrapper study = (StudyWrapper) getModelObject();
        return study.getNameShort();
    }

    @Override
    public String getTooltipText() {
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
    public boolean isDeletable() {
        return internalIsDeletable();
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return StudyEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return StudyViewForm.ID;
    }
}
