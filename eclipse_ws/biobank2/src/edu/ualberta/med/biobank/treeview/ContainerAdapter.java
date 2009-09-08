package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.dialogs.MoveContainerDialog;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.ContainerViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class ContainerAdapter extends AdapterBase {

    public ContainerAdapter(AdapterBase parent, Container container) {
        super(parent, container, Container.class);
        setHasChildren(container.getChildPositionCollection() != null
            && container.getChildPositionCollection().size() > 0);
    }

    @Override
    protected Integer getModelObjectId() {
        return getContainer().getId();
    }

    public Container getContainer() {
        return (Container) getWrappedObject();
    }

    public void setContainer(Container container) {
        setWrappedObject(container, Container.class);
    }

    @Override
    public Integer getId() {
        Container container = getContainer();
        Assert.isNotNull(container, "container is null");
        return container.getId();
    }

    @Override
    public String getName() {
        Container container = getContainer();
        Assert.isNotNull(container, "container is null");
        if (container.getContainerType() == null) {
            return container.getLabel();
        }
        return container.getLabel() + " ("
            + container.getContainerType().getName() + ")";
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
        mi.setText("Move Container");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                MoveContainerDialog mc = new MoveContainerDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    getContainer());

                if (mc.open() == Dialog.OK) {
                    Container container = mc.getContainer();
                    container.setLabel(mc.getAddress());
                    SDKQuery c = new UpdateExampleQuery(mc.getContainer());
                    try {
                        ContainerAdapter.this.getAppService().executeQuery(c);
                    } catch (ApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
        try {
            // read from database again
            Container container = (Container) loadWrappedObject();
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
                    + getContainer().getLabel(), e);
        }
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getTreeText() {
        return getName();
    }

    @Override
    protected boolean integrityCheck() {
        Container c = getContainer();
        if (c != null)
            if ((c.getContainerType() != null && c.getContainerType()
                .getCapacity() != null)
                || c.getContainerType() == null)
                if ((c.getPosition() != null
                    && c.getPosition().getRow() != null && c.getPosition()
                    .getCol() != null)
                    || c.getPosition() == null)
                    if (c.getSite() != null)
                        return true;
        return false;

    }
}
