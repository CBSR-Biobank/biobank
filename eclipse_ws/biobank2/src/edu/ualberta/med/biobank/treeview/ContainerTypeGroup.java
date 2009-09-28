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
import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ContainerTypeComparator;
import edu.ualberta.med.biobank.model.Site;

public class ContainerTypeGroup extends AdapterBase {

    public ContainerTypeGroup(SiteAdapter parent, int id) {
        super(parent, id, "Container Types", true);
    }

    @Override
    protected Integer getWrappedObjectId() {
        Assert.isTrue(false, "Should not be invoked for this type of adatper");
        return null;
    }

    @Override
    public void performDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add Container Type");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                ContainerType ct = new ContainerType();
                ct.setSite(getParentFromClass(SiteAdapter.class).getSite());
                ContainerTypeAdapter adapter = new ContainerTypeAdapter(
                    ContainerTypeGroup.this, ct);
                openForm(new FormInput(adapter), ContainerTypeEntryForm.ID);
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

            List<ContainerType> containerTypes = new ArrayList<ContainerType>(
                currentSite.getContainerTypeCollection());
            Collections.sort(containerTypes, new ContainerTypeComparator());
            SessionManager.getLogger().trace(
                "updateStudies: Site " + currentSite.getName() + " has "
                    + containerTypes.size() + " studies");

            for (ContainerType containerType : containerTypes) {
                SessionManager.getLogger().trace(
                    "updateStudies: Container Type " + containerType.getId()
                        + ": " + containerType.getName());

                ContainerTypeAdapter node = (ContainerTypeAdapter) getChild(containerType
                    .getId());

                if (node == null) {
                    node = new ContainerTypeAdapter(this, containerType);
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().updateTreeNode(node);
                }
            }
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading storage type group children for site "
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
