package edu.ualberta.med.biobank.treeview.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public class ContainerGroup extends AdapterBase {

    private static BgcLogger logger = BgcLogger
        .getLogger(ContainerGroup.class.getName());

    public ContainerGroup(SiteAdapter parent, int id) {
        super(parent, id, "Containers", true, true);
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
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(ContainerWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Add a Container");
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
    public String getTooltipText() {
        return null;
    }

    @Override
    public List<AdapterBase> search(Object searchedObject) {
        List<AdapterBase> res = new ArrayList<AdapterBase>();
        if (searchedObject instanceof ContainerWrapper) {
            ContainerWrapper container = (ContainerWrapper) searchedObject;
            if (container.getContainerType() != null) {
                if (Boolean.TRUE.equals(container.getContainerType()
                    .getTopLevel())) {
                    AdapterBase child = getChild(
                        (ModelWrapper<?>) searchedObject, true);
                    if (child != null)
                        res.add(child);
                } else {
                    List<ContainerWrapper> parents = new ArrayList<ContainerWrapper>();
                    ContainerWrapper currentContainer = container;
                    while (currentContainer.hasParentContainer()) {
                        currentContainer = currentContainer
                            .getParentContainer();
                        parents.add(currentContainer);
                    }
                    for (AdapterBase child : getChildren()) {
                        if (child instanceof ContainerAdapter) {
                            res = searchChildContainers(searchedObject,
                                (ContainerAdapter) child, parents);
                        } else {
                            res = child.search(searchedObject);
                        }
                        if (res.size() > 0)
                            break;
                    }
                    if (res.size() == 0)
                        res = searchChildren(searchedObject);
                }
            }
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
        SiteWrapper parentSite = ((SiteAdapter) getParent()).getWrapper();
        Assert.isNotNull(parentSite, "site null");
        parentSite.reload();
        return parentSite.getTopContainerCollection();
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return getWrapperChildren().size();
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }

    public void addContainer(SiteAdapter siteAdapter, boolean hasPreviousForm) {
        try {
            List<ContainerTypeWrapper> top = ContainerTypeWrapper
                .getTopContainerTypesInSite(SessionManager.getAppService(),
                    siteAdapter.getWrapper());
            if (top.size() == 0) {
                MessageDialog
                    .openError(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        "Unable to create container",
                        "You must define a top-level container type before initializing storage.");
            } else {
                ContainerWrapper c = new ContainerWrapper(
                    SessionManager.getAppService());
                c.setSite(siteAdapter.getWrapper());
                ContainerAdapter adapter = new ContainerAdapter(
                    siteAdapter.getContainersGroupNode(), c);
                adapter.openEntryForm(hasPreviousForm);
            }
        } catch (final RemoteConnectFailureException exp) {
            BgcPlugin.openRemoteConnectErrorMessage(exp);
        } catch (Exception e) {
            logger.error("BioBankFormBase.createPartControl Error", e);
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
}
