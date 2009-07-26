package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.ContainerViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.Site;

public class ContainerAdapter extends Node {

    private Container container;

    public ContainerAdapter(Node parent, Container container) {
        super(parent);
        this.container = container;
        setHasChildren(container.getChildPositionCollection() != null
            && container.getChildPositionCollection().size() > 0);
    }

    @Override
    public Integer getId() {
        Assert.isNotNull(container, "container is null");
        return container.getId();
    }

    @Override
    public String getName() {
        Assert.isNotNull(container, "container is null");
        return container.getPositionCode();
    }

    @Override
    public String getTitle() {
        return getTitle("Container");
    }

    @Override
    public void performDoubleClick() {
        openForm(new FormInput(this), ContainerViewForm.ID);
        performExpand();
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Edit Container");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ContainerAdapter.this),
                    ContainerEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("View Container");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(ContainerAdapter.this),
                    ContainerViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add a Child Container");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                ContainerAdapter adapter = new ContainerAdapter(
                    ContainerAdapter.this, ModelUtils.newContainer(container));
                openForm(new FormInput(adapter), ContainerEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
        try {
            // read from database again
            container = (Container) ModelUtils.getObjectWithId(getAppService(),
                Container.class, container.getId());
            for (ContainerPosition childPosition : container
                .getChildPositionCollection()) {
                Container child = childPosition.getContainer();
                ContainerAdapter node = (ContainerAdapter) getChild(child
                    .getId());

                if (node == null) {
                    node = new ContainerAdapter(this, child);
                    addChild(node);
                }
                if (updateNode) {
                    SessionManager.getInstance().getTreeViewer().update(node,
                        null);
                }
            }
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading storage container group children for storage container "
                    + container.getPositionCode(), e);
        }
    }

    @Override
    public Node accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    public Site getSite() {
        Node parent = getParent();
        if (parent instanceof ContainerAdapter) {
            return ((ContainerAdapter) parent).getSite();
        } else if (parent instanceof ContainerGroup) {
            return ((SiteAdapter) ((ContainerGroup) parent).getParent())
                .getSite();
        }
        return null;
    }

    @Override
    public String getTreeText() {
        return getName() + " (" + container.getContainerType().getName() + ")";
    }

}
