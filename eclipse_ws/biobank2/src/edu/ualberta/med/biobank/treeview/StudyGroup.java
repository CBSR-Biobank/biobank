package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class StudyGroup extends AdapterBase {

    private static Logger LOGGER = Logger.getLogger(StudyGroup.class.getName());

    public StudyGroup(SiteAdapter parent, int id) {
        super(parent, id, "Studies", true);
    }

    public void openViewForm() {
        Assert.isTrue(false, "should not be called");
    }

    @Override
    public void performDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add Study");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                StudyWrapper study = new StudyWrapper(parent.getAppService());
                study.setSite(getParentFromClass(SiteAdapter.class)
                    .getWrapper());
                StudyAdapter adapter = new StudyAdapter(StudyGroup.this, study);
                openForm(new FormInput(adapter), StudyEntryForm.ID);
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
        SiteWrapper currentSite = ((SiteAdapter) getParent()).getWrapper();
        Assert.isNotNull(currentSite, "null site");
        try {
            // read from database again
            currentSite.reload();

            List<StudyWrapper> studies = currentSite.getStudyCollection(true);
            if (studies != null)
                for (StudyWrapper study : studies) {
                    StudyAdapter node = (StudyAdapter) getChild(study.getId());

                    if (node == null) {
                        // first time building the tree
                        node = new StudyAdapter(this, study);
                        addChild(node);
                    }
                    if (updateNode) {
                        SessionManager.getInstance().updateTreeNode(node);
                    }
                }
        } catch (Exception e) {
            LOGGER.error("Error while loading study group children for site "
                + currentSite.getName(), e);
        }
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getTitle() {
        return null;
    }

}
