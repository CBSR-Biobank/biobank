package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.site.SiteGetTopContainersAction;
import edu.ualberta.med.biobank.common.permission.container.ContainerCreatePermission;
import edu.ualberta.med.biobank.common.permission.container.ContainerDeletePermission;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.common.permission.container.ContainerUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public class ContainerGroup extends AdapterBase {
    private static final I18n i18n = I18nFactory.getI18n(ContainerGroup.class);

    private static BgcLogger log = BgcLogger.getLogger(ContainerGroup.class.getName());

    private List<Container> topContainers = null;

    private final boolean createAllowed;

    public ContainerGroup(SiteAdapter parent, int id) {
        super(parent, id, Container.NAME.plural().toString(), true);

        this.createAllowed = isAllowed(new ContainerCreatePermission(parent.getId()));
        this.isDeletable = isAllowed(new ContainerDeletePermission());
        this.isReadable = isAllowed(new ContainerReadPermission(parent.getId()));
        this.isEditable = isAllowed(new ContainerUpdatePermission(
            (Site) parent.getModelObject().getWrappedObject()));
    }

    public boolean isCreateAllowed() {
        return createAllowed;
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
    }

    @Override
    public void performExpand() {
        final SiteAdapter siteAdapter = (SiteAdapter) getParent();
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @SuppressWarnings("nls")
            @Override
            public void run() {
                try {
                    topContainers = SessionManager.getAppService().doAction(
                        new SiteGetTopContainersAction(
                            siteAdapter.getId())).getList();
                    ContainerGroup.super.performExpand();
                } catch (Exception e) {
                    String text = getClass().getName();
                    if (getModelObject() != null) {
                        text = getModelObject().toString();
                    }
                    log.error(
                        "Error while loading children of node " + text, e);
                }
            }
        });
    }

    @SuppressWarnings("nls")
    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(
                // menu item label.
                i18n.tr("Add a Container"));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addContainer(ContainerGroup.this
                        .getParentFromClass(SiteAdapter.class), false);
                }
            });
        }
    }

    @Override
    public String getTooltipTextInternal() {
        return null;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId,
            ContainerTypeWrapper.class);
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
        final SiteAdapter siteAdapter = (SiteAdapter) getParent();
        topContainers = SessionManager.getAppService().doAction(
            new SiteGetTopContainersAction(siteAdapter.getId())).getList();
        return ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
            topContainers, ContainerWrapper.class);
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }

    @SuppressWarnings("nls")
    public void addContainer(SiteAdapter siteAdapter, boolean hasPreviousForm) {
        try {
            SiteWrapper site = (SiteWrapper) siteAdapter.getModelObject();
            List<ContainerTypeWrapper> top = ContainerTypeWrapper
                .getTopContainerTypesInSite(SessionManager.getAppService(),
                    site);
            if (top.size() == 0) {
                BgcPlugin
                    .openError(
                        // dialog title.
                        i18n.tr("Unable to create container"),
                        // dialog message.
                        i18n.tr("You must define a top-level container type before initializing storage."));
            } else {
                ContainerWrapper c = new ContainerWrapper(
                    SessionManager.getAppService());
                c.setSite(site);
                ContainerAdapter adapter = new ContainerAdapter(
                    siteAdapter.getContainersGroupNode(), c);
                adapter.openEntryForm(hasPreviousForm);
            }
        } catch (final RemoteConnectFailureException exp) {
            BgcPlugin.openRemoteConnectErrorMessage(exp);
        } catch (Exception e) {
            log.error("ContainerGroup.addContainer Error", e);
        }
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
