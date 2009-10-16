package edu.ualberta.med.biobank.treeview;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.dialogs.MoveContainerDialog;
import edu.ualberta.med.biobank.dialogs.SelectParentContainerDialog;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.ContainerViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class ContainerAdapter extends AdapterBase {

    private static Logger LOGGER = Logger.getLogger(SessionManager.class
        .getName());

    public ContainerAdapter(AdapterBase parent, ContainerWrapper container) {
        super(parent, container);
        setHasChildren(container.hasChildren());
    }

    public ContainerWrapper getContainer() {
        return (ContainerWrapper) modelObject;
    }

    @Override
    public String getName() {
        ContainerWrapper container = getContainer();
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

        if (!getContainer().getContainerType().getTopLevel()) {
            mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Move Container");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    moveAction();
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }
        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Delete Container");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                delete("Are you sure you want to delete this container?");
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
        try {
            List<ContainerWrapper> children = getContainer().getChildren();
            if (children != null) {
                // read from database again
                for (ContainerWrapper child : children) {
                    ContainerAdapter node = (ContainerAdapter) getChild(child
                        .getId());
                    if (node == null) {
                        node = new ContainerAdapter(this, child);
                        addChild(node);
                    }
                    if (updateNode) {
                        SessionManager.getInstance().updateTreeNode(node);
                    }
                }
            } else
                throw new Exception("Children null.");
        } catch (Exception e) {
            LOGGER.error(
                "Error while loading storage container group children for storage container "
                    + getContainer().getLabel(), e);
        }
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    private void moveAction() {
        final ContainerAdapter oldParent = (ContainerAdapter) getParent();
        final MoveContainerDialog mc = new MoveContainerDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            getContainer());
        if (mc.open() == Dialog.OK) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    try {
                        setNewPositionFromLabel(mc.getNewLabel());

                        // update new parent
                        ContainerWrapper newParentContainer = getContainer()
                            .getParent();
                        ContainerAdapter parentAdapter = (ContainerAdapter) SessionManager
                            .getInstance().searchNode(newParentContainer);
                        if (parentAdapter != null) {
                            parentAdapter.getContainer().reload();
                            parentAdapter.performExpand();
                        }
                        // update old parent
                        oldParent.getContainer().reload();
                        oldParent.removeAll();
                        oldParent.performExpand();
                    } catch (Exception e) {
                        BioBankPlugin.openError(e.getMessage(), e);
                    }
                }
            });
        }
    }

    /**
     * if address exists and if address is not full and if type is valid for
     * slot: modify this object's position, label and the label of children
     */
    public void setNewPositionFromLabel(String newLabel) throws Exception {
        ContainerWrapper container = getContainer();
        String oldLabel = container.getLabel();
        String newParentContainerLabel = newLabel.substring(0, newLabel
            .length() - 2);
        List<ContainerWrapper> newParentContainers = container
            .getPossibleParents(newParentContainerLabel);
        if (newParentContainers.size() == 0) {
            // invalid parent
            throw new Exception(
                "Unable to find suitable parent container with label "
                    + newParentContainerLabel + ".");
        } else {
            ContainerWrapper newParent = newParentContainers.get(0);
            if (newParentContainers.size() > 1) {
                SelectParentContainerDialog dlg = new SelectParentContainerDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), newParentContainers);
                if (dlg.open() == Dialog.OK) {
                    newParent = dlg.getSelectedContainer();
                } else
                    return;
            }
            container.assignNewParent(newParent, newLabel);
        }
        BioBankPlugin.openInformation("Container moved", "The container "
            + oldLabel + " has been moved to " + container.getLabel());
    }

}
