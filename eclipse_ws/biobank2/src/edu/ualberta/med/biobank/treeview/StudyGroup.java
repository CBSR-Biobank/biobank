package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

public class StudyGroup extends Node {

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
                StudyAdapter adapter = new StudyAdapter(StudyGroup.this, study);
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
            currentSite = (Site) ModelUtils.getObjectWithId(getAppService(),
                Site.class, currentSite.getId());
            ((SiteAdapter) getParent()).setSite(currentSite);

            Collection<Study> studies = currentSite.getStudyCollection();
            SessionManager.getLogger().trace(
                "updateStudies: Site " + currentSite.getName() + " has "
                    + studies.size() + " studies");

            for (Study study : studies) {
                SessionManager.getLogger().trace(
                    "updateStudies: Study " + study.getId() + ": "
                        + study.getName() + ", short name: "
                        + study.getNameShort());

                StudyAdapter node = (StudyAdapter) getChild(study.getId());

                if (node == null) {
                    // first time building the tree
                    node = new StudyAdapter(this, study);
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().getTreeViewer().update(node,
                        null);
                }
            }
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading study group children for site "
                    + currentSite.getName(), e);
        }
    }

    @Override
    public Node accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getTitle() {
        return null;
    }
}
