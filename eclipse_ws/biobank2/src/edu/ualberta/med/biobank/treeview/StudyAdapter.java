package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class StudyAdapter extends AdapterBase {

    public static final int PATIENTS_NODE_ID = 0;

    public StudyAdapter(AdapterBase parent, StudyWrapper studyWrapper,
        boolean enabledActions) {
        super(parent, studyWrapper);
        this.enableActions = enabledActions;

        if (studyWrapper.getId() != null) {
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
    public String getName() {
        StudyWrapper study = getWrapper();
        Assert.isNotNull(study, "study is null");
        return study.getNameShort();
    }

    @Override
    public String getTitle() {
        return getTitle("Study");
    }

    @Override
    public void performDoubleClick() {
        if (enableActions) {
            openForm(new FormInput(this), StudyViewForm.ID);
        }
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Study", StudyEntryForm.ID);
        addViewMenu(menu, "Study", StudyViewForm.ID);
        addDeleteMenu(menu, "Study",
            "Are you sure you want to delete this study?");
    }

    @Override
    public void loadChildren(boolean updateNode) {
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

}
