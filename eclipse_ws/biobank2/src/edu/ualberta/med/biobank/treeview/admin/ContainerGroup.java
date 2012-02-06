package edu.ualberta.med.biobank.treeview.admin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.site.SiteGetTopContainersAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetTopContainersResult;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerGroup extends AdapterBase {

    private SiteGetTopContainersResult topContainerResult;

    private static BgcLogger logger = BgcLogger.getLogger(ContainerGroup.class
        .getName());

    public ContainerGroup(SiteAdapter parent, int id) {
        super(parent, id, Messages.ContainerGroup_containers_node_label, true,
            true);
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
        SiteAdapter siteAdapter = (SiteAdapter) getParent();
        try {
            topContainerResult = SessionManager.getAppService().doAction(
                new SiteGetTopContainersAction(siteAdapter.getId()));
            super.performExpand();
        } catch (ApplicationException e) {
            // TODO: open an error dialog here?
            logger.error("BioBankFormBase.createPartControl Error", e); //$NON-NLS-1$            
        }
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(ContainerWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.ContainerGroup_add_label);
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
        List<AbstractAdapterBase> res = new ArrayList<AbstractAdapterBase>();
        if (ContainerWrapper.class.isAssignableFrom(searchedClass)) {
            // FIXME search might need to be different now
            // ContainerWrapper container = (ContainerWrapper) searchedObject;
            // if (container.getContainerType() != null) {
            // if (Boolean.TRUE.equals(container.getContainerType()
            // .getTopLevel())) {
            // AbstractAdapterBase child = getChild(objectId, true);
            // if (child != null)
            // res.add(child);
            // } else {
            // List<ContainerWrapper> parents = new
            // ArrayList<ContainerWrapper>();
            // ContainerWrapper currentContainer = container;
            // while (currentContainer.hasParentContainer()) {
            // currentContainer = currentContainer
            // .getParentContainer();
            // parents.add(currentContainer);
            // }
            // for (AbstractAdapterBase child : getChildren()) {
            // if (child instanceof ContainerAdapter) {
            // res = searchChildContainers(searchedObject,
            // objectId, (ContainerAdapter) child, parents);
            // } else {
            // res = child.search(searchedObject, objectId);
            // }
            // if (res.size() > 0)
            // break;
            // }
            // if (res.size() == 0)
            // res = searchChildren(searchedObject, objectId);
            // }
            // }
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

        for (Container container : topContainerResult.getTopContainers()) {
            ContainerWrapper wrapper =
                new ContainerWrapper(SessionManager.getAppService(), container);
            result.add(wrapper);
        }

        return result;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return topContainerResult.getTopContainers().size();
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }

    public void addContainer(SiteAdapter siteAdapter, boolean hasPreviousForm) {
        try {
            SiteWrapper site = (SiteWrapper) siteAdapter.getModelObject();
            List<ContainerTypeWrapper> top = ContainerTypeWrapper
                .getTopContainerTypesInSite(SessionManager.getAppService(),
                    site);
            if (top.size() == 0) {
                BgcPlugin.openError(Messages.ContainerGroup_create_error_title,
                    Messages.ContainerGroup_create_error_msg);
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
            logger.error("BioBankFormBase.createPartControl Error", e); //$NON-NLS-1$
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
