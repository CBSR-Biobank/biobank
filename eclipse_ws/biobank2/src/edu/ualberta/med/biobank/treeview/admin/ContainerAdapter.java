package edu.ualberta.med.biobank.treeview.admin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.container.ContainerChildrenResult;
import edu.ualberta.med.biobank.common.action.container.ContainerGetChildrenAction;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.MoveContainerDialog;
import edu.ualberta.med.biobank.dialogs.MoveSpecimensToDialog;
import edu.ualberta.med.biobank.dialogs.select.SelectParentContainerDialog;
import edu.ualberta.med.biobank.forms.ContainerEntryForm;
import edu.ualberta.med.biobank.forms.ContainerViewForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerAdapter extends AdapterBase {

    private static BgcLogger LOGGER = BgcLogger
        .getLogger(AbstractAdapterBase.class.getName());

    private ContainerChildrenResult childrenResult;

    public ContainerAdapter(AdapterBase parent, ContainerWrapper container) {
        super(parent, container);
        // assume it has children for now and set it appropriately when user
        // double clicks on node
        if (container != null) {
            setHasChildren(true);
        }
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
        openViewForm();
    }

    @Override
    public void performExpand() {
        try {
            childrenResult = SessionManager.getAppService().doAction(
                new ContainerGetChildrenAction(getId()));
            super.performExpand();
        } catch (ApplicationException e) {
            // TODO: open an error dialog here?
            LOGGER.error("BioBankFormBase.createPartControl Error", e); //$NON-NLS-1$            
        }
    }

    @Override
    public void setModelObject(Object modelObject) {
        super.setModelObject(modelObject);
        // assume it has children for now and set it appropriately when user
        // double clicks on node
        setHasChildren(true);
    }

    private ContainerWrapper getContainer() {
        return (ContainerWrapper) getModelObject();
    }

    @Override
    protected String getLabelInternal() {
        ContainerWrapper container = getContainer();
        if (container.getContainerType() == null) {
            return container.getLabel();
        }
        return container.getLabel() + " (" //$NON-NLS-1$
            + container.getContainerType().getNameShort() + ")"; //$NON-NLS-1$
    }

    @Override
    public String getTooltipTextInternal() {
        ContainerWrapper container = getContainer();
        if (container != null) {
            SiteWrapper site = container.getSite();
            if (site != null) {
                return site.getNameShort() + " - " //$NON-NLS-1$
                    + getTooltipText(Messages.ContainerAdapter_container_label);
            }
        }
        return getTooltipText(Messages.ContainerAdapter_container_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Messages.ContainerAdapter_container_label);
        addViewMenu(menu, Messages.ContainerAdapter_container_label);

        Boolean topLevel = getContainer().getContainerType().getTopLevel();
        if (isEditable() && (topLevel == null || !topLevel)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.ContainerAdapter_move_label);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    moveContainer(null);
                }
            });
        }

        if (isEditable() && getContainer().hasSpecimens()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.ContainerAdapter_move_specs_label);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    moveSpecimens();
                }
            });
        }

        addDeleteMenu(menu, Messages.ContainerAdapter_container_label);
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
                        monitor.beginTask(NLS.bind(
                            Messages.ContainerAdapter_moving_specs,
                            getContainer().getFullInfoLabel(),
                            newContainer.getFullInfoLabel()),
                            IProgressMonitor.UNKNOWN);
                        try {
                            getContainer().moveSpecimens(newContainer);
                            // newContainer.persist();
                            newContainer.reload();
                            monitor.done();
                            BgcPlugin
                                .openAsyncInformation(
                                    Messages.ContainerAdapter_spec_moved_info_title,
                                    NLS.bind(
                                        Messages.ContainerAdapter_spec_moved_info_msg,
                                        newContainer.getSpecimens().size(),
                                        newContainer.getFullInfoLabel()));
                        } catch (Exception e) {
                            monitor.setCanceled(true);
                            BgcPlugin.openAsyncError(
                                Messages.ContainerAdapter_move_erro_title, e);
                        }
                    }
                });
                ContainerAdapter newContainerAdapter =
                    (ContainerAdapter) SessionManager
                        .searchFirstNode(ContainerWrapper.class,
                            newContainer.getId());
                if (newContainerAdapter != null) {
                    getContainer().reload();
                    newContainerAdapter.performDoubleClick();
                }
                getContainer().reload();
                SessionManager.openViewForm(getContainer());
            } catch (Exception e) {
                BgcPlugin.openError(
                    Messages.ContainerAdapter_move_specs_error_title, e);
            }
        }
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.ContainerAdapter_delete_confirm_msg;
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
                    ContainerAdapter parentAdapter =
                        (ContainerAdapter) SessionManager
                            .searchFirstNode(ContainerWrapper.class,
                                newParentContainer.getId());
                    if (parentAdapter != null) {
                        parentAdapter.getContainer().reload();
                        parentAdapter.removeAll();
                        parentAdapter.performExpand();
                    }
                    // update old parent
                    oldParent.getContainer().reload();
                    oldParent.removeAll();
                    oldParent.performExpand();
                }
            } catch (Exception e) {
                BgcPlugin.openError(
                    Messages.ContainerAdapter_move_cont_error_title, e);
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
            BgcPlugin.openError(Messages.ContainerAdapter_move_error_title,
                NLS.bind(Messages.ContainerAdapter_move_parent_error_msg,
                    newLabel));
            return false;
        }

        ContainerWrapper newParent;
        if (newParentContainers.size() > 1) {
            SelectParentContainerDialog dlg =
                new SelectParentContainerDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(),
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
            BgcPlugin
                .openError(Messages.ContainerAdapter_move_error_title, NLS
                    .bind(Messages.ContainerAdapter_move_empty_error_msg,
                        newLabel));
            return false;
        }

        newParent.addChild(newLabel.substring(newParent.getLabel().length()),
            container);

        IRunnableContext context = new ProgressMonitorDialog(Display
            .getDefault().getActiveShell());
        context.run(true, false, new IRunnableWithProgress() {
            @Override
            public void run(final IProgressMonitor monitor) {
                monitor.beginTask(NLS.bind(
                    Messages.ContainerAdapter_moving_cont, oldLabel, newLabel),
                    IProgressMonitor.UNKNOWN);
                try {
                    container.persist();
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        Messages.ContainerAdapter_move_error_title, e);
                }
                monitor.done();
                BgcPlugin.openAsyncInformation(
                    Messages.ContainerAdapter_cont_moved_info_title, NLS.bind(
                        Messages.ContainerAdapter_moved_info_msg, oldLabel,
                        container.getLabel()));
            }
        });
        return true;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        List<AbstractAdapterBase> res = new ArrayList<AbstractAdapterBase>();
        if (ContainerWrapper.class.isAssignableFrom(searchedClass)) {
            // FIXME search might need to be different now
            // ContainerWrapper containerWrapper = (ContainerWrapper)
            // searchedObject;
            // List<ContainerWrapper> parents = new
            // ArrayList<ContainerWrapper>();
            // ContainerWrapper currentContainer = containerWrapper;
            // while (currentContainer.hasParentContainer()) {
            // currentContainer = currentContainer.getParentContainer();
            // parents.add(currentContainer);
            // }
            // res = searchChildContainers(searchedObject, objectId, this,
            // parents);
        }
        return res;
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ContainerAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        Assert.isTrue(child instanceof ContainerWrapper);
        return new ContainerAdapter(this, (ContainerWrapper) child);
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        List<ContainerWrapper> result = new ArrayList<ContainerWrapper>();

        for (Container container : childrenResult.getChildContainers()) {
            ContainerWrapper wrapper =
                new ContainerWrapper(SessionManager.getAppService(), container);
            result.add(wrapper);
        }

        return result;
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

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof ContainerAdapter)
            return internalCompareTo(o);
        return 0;
    }
}
