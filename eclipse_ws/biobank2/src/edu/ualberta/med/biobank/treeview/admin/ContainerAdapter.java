package edu.ualberta.med.biobank.treeview.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.MoveContainerDialog;
import edu.ualberta.med.biobank.dialogs.MoveSpecimensToDialog;
import edu.ualberta.med.biobank.dialogs.select.SelectParentContainerDialog;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.ContainerViewForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ContainerAdapter extends AdapterBase {

    public ContainerAdapter(AdapterBase parent, ContainerWrapper container) {
        super(parent, container);
        if (container != null) {
            setHasChildren(container.hasChildren());
        }
    }

    @Override
    public void setModelObject(ModelWrapper<?> modelObject) {
        super.setModelObject(modelObject);
        // assume it has children for now and set it appropriately when user
        // double clicks on node
        setHasChildren(true);
    }

    public ContainerWrapper getContainer() {
        return (ContainerWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        ContainerWrapper container = getContainer();
        if (container.getContainerType() == null) {
            return container.getLabel();
        }
        return container.getLabel() + " ("
            + container.getContainerType().getNameShort() + ")";
    }

    @Override
    public String getTooltipText() {
        ContainerWrapper container = getContainer();
        if (container != null) {
            SiteWrapper site = container.getSite();
            if (site != null) {
                return site.getNameShort() + " - "
                    + getTooltipText("Container");
            }
        }
        return getTooltipText("Container");
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
        openViewForm();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Container");
        addViewMenu(menu, "Container");

        Boolean topLevel = getContainer().getContainerType().getTopLevel();
        if (isEditable() && (topLevel == null || !topLevel)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Move container");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    moveContainer(null);
                }
            });
        }

        if (isEditable() && getContainer().hasSpecimens()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Move all specimens to");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    moveSpecimens();
                }
            });
        }

        addDeleteMenu(menu, "Container");
    }

    public void moveSpecimens() {
        final MoveSpecimensToDialog mc = new MoveSpecimensToDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            getContainer());
        if (mc.open() == Dialog.OK) {
            try {
                final ContainerWrapper newContainer = mc.getNewContainer();
                IRunnableContext context = new ProgressMonitorDialog(Display
                    .getDefault().getActiveShell());
                context.run(true, false, new IRunnableWithProgress() {
                    @Override
                    public void run(final IProgressMonitor monitor) {
                        monitor.beginTask("Moving specimens from container "
                            + getContainer().getFullInfoLabel() + " to "
                            + newContainer.getFullInfoLabel(),
                            IProgressMonitor.UNKNOWN);
                        try {
                            getContainer().moveSpecimens(newContainer);
                            // newContainer.persist();
                            newContainer.reload();
                            monitor.done();
                            BiobankPlugin.openAsyncInformation(
                                "Specimens moved", newContainer.getSpecimens()
                                    .size()
                                    + " specimens are now in "
                                    + newContainer.getFullInfoLabel() + ".");
                        } catch (Exception e) {
                            BiobankPlugin.openAsyncError("Move problem", e);
                        }
                        monitor.done();
                        BiobankPlugin.openAsyncInformation(
                            "Specimens moved",
                            newContainer.getSpecimens().size()
                                + " specimens are now in "
                                + newContainer.getFullInfoLabel() + ".");
                    }
                });
                ContainerAdapter newContainerAdapter = (ContainerAdapter) SessionManager
                    .searchFirstNode(newContainer);
                if (newContainerAdapter != null) {
                    getContainer().reload();
                    newContainerAdapter.performDoubleClick();
                }
                getContainer().reload();
                SessionManager.openViewForm(getContainer());
            } catch (Exception e) {
                BiobankPlugin.openError("Problem while moving specimens", e);
            }
        }
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this container?";
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

    public void moveContainer(ContainerWrapper destParentContainer) {
        final ContainerAdapter oldParent = (ContainerAdapter) getParent();
        final MoveContainerDialog mc = new MoveContainerDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            getContainer(), destParentContainer);
        if (mc.open() == Dialog.OK) {
            try {
                if (setNewPositionFromLabel(mc.getNewLabel())) {
                    // update new parent
                    ContainerWrapper newParentContainer = getContainer()
                        .getParentContainer();
                    ContainerAdapter parentAdapter = (ContainerAdapter) SessionManager
                        .searchFirstNode(newParentContainer);
                    if (parentAdapter != null) {
                        parentAdapter.getContainer().reload();
                        parentAdapter.performExpand();
                    }
                    // update old parent
                    oldParent.getContainer().reload();
                    oldParent.removeAll();
                    oldParent.performExpand();
                }
            } catch (Exception e) {
                BiobankPlugin.openError("Problem while moving container", e);
            }
        }
    }

    /**
     * if address exists and if address is not full and if type is valid for
     * slot: modify this object's position, label and the label of children
     */
    public boolean setNewPositionFromLabel(final String newLabel)
        throws Exception {
        final ContainerWrapper container = getContainer();
        final String oldLabel = container.getLabel();
        List<ContainerWrapper> newParentContainers = container
            .getPossibleParents(newLabel);
        if (newParentContainers.size() == 0) {
            BiobankPlugin.openError("Move Error",
                "A parent container with child \"" + newLabel
                    + "\" does not exist.");
            return false;
        }

        ContainerWrapper newParent;
        if (newParentContainers.size() > 1) {
            SelectParentContainerDialog dlg = new SelectParentContainerDialog(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                newParentContainers);
            if (dlg.open() != Dialog.OK) {
                return false;
            }
            newParent = dlg.getSelectedContainer();
        } else {
            newParent = newParentContainers.get(0);
        }

        ContainerWrapper currentChild = newParent.getChildByLabel(newLabel);
        if (currentChild != null) {
            BiobankPlugin.openError("Move Error", "Container position \""
                + newLabel
                + "\" is not empty. Please choose a different location.");
            return false;
        }

        newParent.addChild(newLabel.substring(newParent.getLabel().length()),
            container);

        IRunnableContext context = new ProgressMonitorDialog(Display
            .getDefault().getActiveShell());
        context.run(true, false, new IRunnableWithProgress() {
            @Override
            public void run(final IProgressMonitor monitor) {
                monitor.beginTask("Moving container " + oldLabel + " to "
                    + newLabel, IProgressMonitor.UNKNOWN);
                try {
                    container.persist();
                } catch (Exception e) {
                    BiobankPlugin.openAsyncError("Move problem", e);
                }
                monitor.done();
                BiobankPlugin.openAsyncInformation("Container moved",
                    "The container " + oldLabel + " has been moved to "
                        + container.getLabel());
            }
        });
        return true;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        List<AdapterBase> res = new ArrayList<AdapterBase>();
        if (searchedObject instanceof ContainerWrapper) {
            ContainerWrapper containerWrapper = (ContainerWrapper) searchedObject;
            List<ContainerWrapper> parents = new ArrayList<ContainerWrapper>();
            ContainerWrapper currentContainer = containerWrapper;
            while (currentContainer.hasParentContainer()) {
                currentContainer = currentContainer.getParentContainer();
                parents.add(currentContainer);
            }
            res = searchChildContainers(searchedObject, this, parents);
        }
        return res;
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ContainerAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof ContainerWrapper);
        return new ContainerAdapter(this, (ContainerWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        Assert.isNotNull(modelObject, "site null");
        ((ContainerWrapper) modelObject).reload();
        return getContainer().getChildren().values();
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return (int) getContainer().getChildCount(true);
    }

    @Override
    public String getEntryFormId() {
        return ContainerEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return ContainerViewForm.ID;
    }

}
