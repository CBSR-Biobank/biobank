package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyComparator;

public class StudyGroup extends AdapterBase {

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
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                Study study = new Study();
                study.setSite(getParentFromClass(SiteAdapter.class).getSite());
                StudyAdapter adapter = new StudyAdapter(StudyGroup.this,
                    new StudyWrapper(parent.getAppService(), study));
                openForm(new FormInput(adapter), StudyEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
        Site currentSite = ((SiteAdapter) getParent()).getSite();
        Assert.isNotNull(currentSite, "null site");
        try {
            // read from database again
            currentSite = ModelUtils.getObjectWithId(getAppService(),
                Site.class, currentSite.getId());
            ((SiteAdapter) getParent()).setSite(currentSite);

            List<Study> studies = new ArrayList<Study>(currentSite
                .getStudyCollection());
            Collections.sort(studies, new StudyComparator());
            for (Study study : studies) {
                StudyAdapter node = (StudyAdapter) getChild(study.getId());

                if (node == null) {
                    // first time building the tree
                    node = new StudyAdapter(this, new StudyWrapper(
                        getAppService(), study));
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().updateTreeNode(node);
                }
            }
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading study group children for site "
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
