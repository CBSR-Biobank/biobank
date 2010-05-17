package edu.ualberta.med.biobank.treeview;

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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.MoveContainerDialog;
import edu.ualberta.med.biobank.dialogs.SelectParentContainerDialog;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.ContainerViewForm;

public class ContainerAdapter extends AdapterBase {

    private final String DEL_CONFIRM_MSG = "Are you sure you want to delete this container?";

    public ContainerAdapter(AdapterBase parent, ContainerWrapper container) {
        super(parent, container);
        if (container != null) {
            setHasChildren(container.hasChildren());
        }
    }

    @Override
    public void setModelObject(ModelWrapper<?> modelObject) {
        super.setModelObject(modelObject);
        setHasChildren(((ContainerWrapper) modelObject).hasChildren());
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
        SiteWrapper site = container.getSite();
        if (site != null) {
            return site.getNameShort() + " - " + getTooltipText("Container");
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
        if (topLevel == null || !topLevel) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Move Container");
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    moveAction();
                }
            });
        }

        addDeleteMenu(menu, "Container", DEL_CONFIRM_MSG);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return DEL_CONFIRM_MSG;
    }

    @Override
    public boolean isDeletable() {
        return true;
    }

    private void moveAction() {
        final ContainerAdapter oldParent = (ContainerAdapter) getParent();
        final MoveContainerDialog mc = new MoveContainerDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            getContainer());
        if (mc.open() == Dialog.OK) {
            try {
                if (setNewPositionFromLabel(mc.getNewLabel())) {
                    // update new parent
                    ContainerWrapper newParentContainer = getContainer()
                        .getParent();
                    ContainerAdapter parentAdapter = (ContainerAdapter) SessionManager
                        .searchNode(newParentContainer);
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
                BioBankPlugin.openError(e.getMessage(), e);
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
        String newParentContainerLabel = newLabel.substring(0, newLabel
            .length() - 2);
        List<ContainerWrapper> newParentContainers = container
            .getPossibleParents(newParentContainerLabel);
        if (newParentContainers.size() == 0) {
            BioBankPlugin.openError("Move Error",
                "A parent container with label \"" + newParentContainerLabel
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
            BioBankPlugin.openError("Move Error", "Container position \""
                + newLabel
                + "\" is not empty. Please choose a different location.");
            return false;
        }

        newParent.addChild(newLabel, container);

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
                    BioBankPlugin.openAsyncError("Move problem", e);
                }
                monitor.done();
                BioBankPlugin.openAsyncInformation("Container moved",
                    "The container " + oldLabel + " has been moved to "
                        + container.getLabel());
            }
        });
        return true;
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
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
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren() {
        return getContainer().getChildren().values();
    }

    @Override
    protected int getWrapperChildCount() {
        return getContainer().getChildCount();
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
