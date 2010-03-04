package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class StudyAdapter extends AdapterBase {

    private final String DEL_CONFIRM_MSG = "Are you sure you want to delete this study?";

    public static final int PATIENTS_NODE_ID = 0;

    public StudyAdapter(AdapterBase parent, StudyWrapper studyWrapper,
        boolean enabledActions) {
        super(parent, studyWrapper);
        this.enableActions = enabledActions;

        if (studyWrapper != null) {
            setId(studyWrapper.getId());
            setName(studyWrapper.getName());
        }
    }

    public StudyAdapter(AdapterBase parent, StudyWrapper studyWrapper) {
        this(parent, studyWrapper, true);
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
        StudyWrapper study = getWrapper();
        SiteWrapper site = study.getSite();
        if (site != null) {
            return site.getNameShort() + " - " + getTooltipText("Study");
        }
        return getTooltipText("Study");
    }

    @Override
    public void executeDoubleClick() {
        if (enableActions) {
            openForm(new FormInput(this), StudyViewForm.ID);
        }
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Study", StudyEntryForm.ID);
        addViewMenu(menu, "Study", StudyViewForm.ID);
        addDeleteMenu(menu, "Study", DEL_CONFIRM_MSG);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return DEL_CONFIRM_MSG;
    }

    @Override
    public boolean isDeletable() {
        return true;
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
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

}
