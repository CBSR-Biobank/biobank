package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.study.StudyDeleteAction;
import edu.ualberta.med.biobank.common.permission.study.StudyDeletePermission;
import edu.ualberta.med.biobank.common.permission.study.StudyReadPermission;
import edu.ualberta.med.biobank.common.permission.study.StudyUpdatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudyAdapter extends AdapterBase {

    public StudyAdapter(AdapterBase parent, StudyWrapper study) {
        super(parent, study);
    }

    @Override
    public void init() {
        try {
            Integer id = ((StudyWrapper) getModelObject()).getId();
            this.isDeletable =
                SessionManager.getAppService().isAllowed(
                    new StudyDeletePermission(id));
            this.isReadable =
                SessionManager.getAppService().isAllowed(
                    new StudyReadPermission(id));
            this.isEditable =
                SessionManager.getAppService().isAllowed(
                    new StudyUpdatePermission(id));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Permission Error",
                "Unable to retrieve user permissions");
        }
    }

    @Override
    protected void setModelObject(Object modelObject) {
        super.setModelObject(modelObject);
    }

    @Override
    protected String getLabelInternal() {
        StudyWrapper study = (StudyWrapper) getModelObject();
        return study.getNameShort();
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(StringUtil.EMPTY_STRING);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, StringUtil.EMPTY_STRING);
        addViewMenu(menu, StringUtil.EMPTY_STRING);
        addDeleteMenu(menu, StringUtil.EMPTY_STRING);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return StringUtil.EMPTY_STRING;
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
        SessionManager.getAppService().doAction(
            new StudyDeleteAction((Study) getModelObject().getWrappedObject()));
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
}
