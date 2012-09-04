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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.container.ContainerDeleteAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetChildrenAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoByLabelAction;
import edu.ualberta.med.biobank.common.action.container.ContainerMoveAction;
import edu.ualberta.med.biobank.common.action.container.ContainerMoveSpecimensAction;
import edu.ualberta.med.biobank.common.permission.container.ContainerDeletePermission;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.common.permission.container.ContainerUpdatePermission;
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

public class ContainerAdapter extends AdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(ContainerAdapter.class);

    @SuppressWarnings("unused")
    private static BgcLogger LOGGER = BgcLogger
        .getLogger(ContainerAdapter.class.getName());

    private List<Container> childContainers = null;

    public ContainerAdapter(AdapterBase parent, ContainerWrapper container) {
        super(parent, container);
        // assume it has children for now and set it appropriately when user
        // double clicks on node
        if (container != null) {
            setHasChildren(true);
        }
    }

    @Override
    public void init() {
        ContainerWrapper container = (ContainerWrapper) getModelObject();
        Integer id = container.getId();

        this.isDeletable = isAllowed(new ContainerDeletePermission(id));
        this.isReadable =
            isAllowed(new ContainerReadPermission(container.getSite().getId()));
        this.isEditable = isAllowed(new ContainerUpdatePermission(id));
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
        openViewForm();
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

    @SuppressWarnings("nls")
    @Override
    protected String getLabelInternal() {
        ContainerWrapper container = getContainer();
        if (container.getContainerType() == null) {
            return container.getLabel();
        }
        return container.getLabel() + " ("
            + container.getContainerType().getNameShort() + ")";
    }

    @SuppressWarnings("nls")
    @Override
    public String getTooltipTextInternal() {
        ContainerWrapper container = getContainer();
        if (container != null) {
            SiteWrapper site = container.getSite();
            if (site != null) {
                return site.getNameShort() + " - "
                    + getTooltipText(Container.NAME.singular().toString());
            }
        }
        return getTooltipText(Container.NAME.singular().toString());
    }

    @SuppressWarnings("nls")
    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Container.NAME.singular().toString());
        addViewMenu(menu, Container.NAME.singular().toString());

        Boolean topLevel = getContainer().getContainerType().getTopLevel();

        if (isEditable() && (topLevel == null || !topLevel)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(
                // menu item label.
                i18n.tr("Move container to"));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    moveContainer(null);
                }
            });
        }

        if (isEditable() && getContainer().hasSpecimens()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(
                // menu item label.
                i18n.tr("Move all specimens to"));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    moveSpecimens();
                }
            });
        }

        addDeleteMenu(menu, Container.NAME.singular().toString());
    }

    @SuppressWarnings("nls")
    public void moveSpecimens() {
        final MoveSpecimensToDialog msDlg =
            new MoveSpecimensToDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), getContainer());
        if (msDlg.open() == Dialog.OK) {
            try {
                @SuppressWarnings("unused")
                final Integer toContainerId = msDlg.getNewContainer().getId();
                final ContainerWrapper newContainer = msDlg.getNewContainer();
                IRunnableContext context =
                    new ProgressMonitorDialog(Display.getDefault()
                        .getActiveShell());
                context.run(true,
                    false,
                    new IRunnableWithProgress() {
                        @Override
                        public void run(final IProgressMonitor monitor) {
                            monitor.beginTask(
                                // progress monitor message.
                                i18n.tr(
                                    "Moving specimens from container {0} to {1}",
                                    getContainer().getFullInfoLabel(),
                                    newContainer.getFullInfoLabel()),
                                IProgressMonitor.UNKNOWN);
                            try {
                                SessionManager.getAppService()
                                    .doAction(new ContainerMoveSpecimensAction(
                                        getContainer().getWrappedObject(),
                                        newContainer.getWrappedObject()));
                                monitor.done();
                                BgcPlugin
                                    .openAsyncInformation(
                                        // dialog title.
                                        i18n.tr("Specimens moved"),
                                        // dialog message.
                                        i18n.tr(
                                            "{0} specimens are now in {1}.",
                                            newContainer.getSpecimens().size(),
                                            newContainer.getFullInfoLabel()));
                            } catch (Exception e) {
                                monitor.setCanceled(true);
                                BgcPlugin
                                    .openAsyncError(
                                        // dialog title.
                                        i18n.tr("Move problem"), e);
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
                BgcPlugin
                    .openError(
                        // dialog title.
                        i18n.tr("Problem while moving specimens"), e);
            }
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected String getConfirmDeleteMessage() {
        // dialog message.
        return i18n.tr("Are you sure you want to delete this container?");
    }

    @SuppressWarnings("nls")
    public void moveContainer(ContainerWrapper destParentContainer) {
        final ContainerAdapter oldParent = (ContainerAdapter) getParent();
        final MoveContainerDialog mc =
            new MoveContainerDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), getContainer(),
                destParentContainer);
        if (mc.open() == Dialog.OK) {
            try {
                if (setNewPositionFromLabel(mc.getNewLabel())) {
                    // update new parent
                    ContainerWrapper newParentContainer =
                        getContainer().getParentContainer();
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
                BgcPlugin
                    .openError(
                        // dialog title.
                        i18n.tr("Problem while moving container"),
                        e);
            }
        }
    }

    /**
     * if address exists and if address is not full and if type is valid for
     * slot: modify this object's position, label and the label of children
     */
    @SuppressWarnings("nls")
    public boolean setNewPositionFromLabel(final String newLabel)
        throws Exception {
        final ContainerWrapper container = getContainer();
        @SuppressWarnings("unused")
        final String oldLabel = container.getLabel();
        List<Container> newParentContainers =
            SessionManager.getAppService().doAction(
                new ContainerGetInfoByLabelAction(newLabel,
                    SessionManager.getUser().getCurrentWorkingSite()
                        .getId())).getList();
        if (newParentContainers.size() == 0) {
            BgcPlugin
                .openError(
                    // dialog title.
                    i18n.tr("Container Move Error"),
                    // dialog message. {0}=label.
                    i18n.tr(
                        "A parent container with child \"{0}\" does not exist.",
                        newLabel));
            return false;
        }

        Container newParent;
        if (newParentContainers.size() > 1) {
            SelectParentContainerDialog dlg =
                new SelectParentContainerDialog(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), newParentContainers);
            if (dlg.open() != Dialog.OK) {
                return false;
            }
            newParent = dlg.getSelectedContainer();
        } else {
            newParent = newParentContainers.get(0);
        }

        SessionManager.getAppService().doAction(new ContainerMoveAction(
            getContainer().getWrappedObject(),
            newParent, newLabel));
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
        childContainers =
            SessionManager.getAppService()
                .doAction(new ContainerGetChildrenAction(getId())).getList();
        return ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
            childContainers, ContainerWrapper.class);
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
        if (o instanceof ContainerAdapter) return internalCompareTo(o);
        return 0;
    }

    @Override
    protected void runDelete() throws Exception {
        SessionManager.getAppService().doAction(new ContainerDeleteAction(
            (Container) getModelObject().getWrappedObject()));
        SessionManager.updateAllSimilarNodes(getParent(), true);
    }
}
