package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.study.StudyDeleteAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class StudyAdapter extends AdapterBase {

    private StudyWrapper study;

    public StudyAdapter(AdapterBase parent, StudyWrapper study) {
        super(parent, study);
        this.study = study;
    }

    @Override
    protected void setModelObject(Object modelObject) {
        super.setModelObject(modelObject);
        this.study = (StudyWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        StudyWrapper study = (StudyWrapper) getModelObject();
        return study.getNameShort();
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
    public boolean isDeletable() {
        // TODO: this needs to be implemented correctly
        return true;
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
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

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof StudyAdapter)
            return internalCompareTo(o);
        return 0;
    }

    @Override
    protected void runDelete() throws Exception {
        // TODO: feedback to the user if this action fails
        SessionManager.getAppService().doAction(new StudyDeleteAction(getId()));
        SessionManager.updateAllSimilarNodes(getParent(), true);
    }

    // Uncomment when we start using MVP again
    //
    // public IEditorPart openEntryForm(boolean hasPreviousForm) {
    // eventBus.fireEvent(new StudyEditEvent(study.getId()));
    // return null; // TODO: problem !?
    // }
    //
    // @Override
    // public void openViewForm() {
    // super.openViewForm();
    // @Override
    // // eventBus.fireEvent(new StudyViewEvent(study.getId()));
    // }

    @Override
    public boolean isEditable() {
        // TODO: this needs to be implemented correctly
        return true;
    }
}
