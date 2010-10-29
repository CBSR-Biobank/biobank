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

    private final String DEL_CONFIRM_MSG = "Are you sure you want to delete this study?";

    public StudyAdapter(AdapterBase parent, StudyWrapper studyWrapper) {
        super(parent, studyWrapper);
        setEditable(parent instanceof StudyMasterGroup || parent == null);
    }

    public StudyWrapper getWrapper() {
        return (StudyWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        StudyWrapper study = getWrapper();
        return study.getNameShort();
    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Study");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Study");
        addViewMenu(menu, "Study");
        addDeleteMenu(menu, "Study", DEL_CONFIRM_MSG);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return DEL_CONFIRM_MSG;
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
