package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.ContainerTypeEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;

public class ContainerTypeGroup extends AdapterBase {

    private static Logger LOGGER = Logger.getLogger(ContainerTypeGroup.class
        .getName());

    public ContainerTypeGroup(SiteAdapter parent, int id) {
        super(parent, id, "Container Types", true);
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
                ContainerTypeWrapper ct = new ContainerTypeWrapper(
                    getAppService());
                ct.setSite(getParentFromClass(SiteAdapter.class).getSite());
                ct.setCapacity(new Capacity());
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
        SiteWrapper currentSite = ((SiteAdapter) getParent()).getWrapper();
        Assert.isNotNull(currentSite, "null site");

        try {
            // read from database again
            currentSite.reload();
            List<ContainerTypeWrapper> containerTypes = new ArrayList<ContainerTypeWrapper>(
                currentSite.getContainerTypeCollection());
            LOGGER.trace("updateStudies: Site " + currentSite.getName()
                + " has " + containerTypes.size() + " studies");

            for (ContainerTypeWrapper containerType : containerTypes) {
                LOGGER.trace("updateStudies: Container Type "
                    + containerType.getId() + ": " + containerType.getName());

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
            LOGGER.error(
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
