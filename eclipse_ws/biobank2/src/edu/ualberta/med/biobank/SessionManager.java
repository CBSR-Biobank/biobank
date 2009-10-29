package edu.ualberta.med.biobank;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.rcp.SiteCombo;
import edu.ualberta.med.biobank.sourceproviders.DebugState;
import edu.ualberta.med.biobank.sourceproviders.SessionState;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.views.SessionsView;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionManager {

    private static Logger LOGGER = Logger.getLogger(SessionManager.class
        .getName());

    private static SessionManager instance = null;

    private SessionsView view;

    private SessionAdapter sessionAdapter;

    private RootNode rootNode;

    private SiteCombo siteCombo;

    private SiteManager siteManager;

    private SessionManager() {
        super();
        rootNode = new RootNode();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSessionsView(SessionsView view) {
        this.view = view;

        updateMenus();
    }

    public void addSession(final WritableApplicationService appService,
        String serverName, String userName, Collection<SiteWrapper> sites) {
        LOGGER.debug("addSession: " + serverName + ", user/" + userName
            + " numSites/" + sites.size());
        sessionAdapter = new SessionAdapter(rootNode, appService, 0,
            serverName, userName);
        rootNode.addChild(sessionAdapter);

        Assert.isNotNull(siteCombo, "site combo is null");

        siteManager = new SiteManager(appService, serverName);
        siteManager.setSiteCombo(siteCombo);
        siteManager.getCurrentSite(serverName, sites);
        siteManager.updateSites(sites);

        sessionAdapter.loadChildren(true);
        if (view != null) {
            view.getTreeViewer().expandToLevel(3);
        }
        updateMenus();
    }

    public void deleteSession() {
        siteManager.setEnabled(false);
        rootNode.removeChild(sessionAdapter);
        sessionAdapter = null;
        updateMenus();
    }

    public void updateSession() {
        Assert.isNotNull(sessionAdapter, "session adapter is null");
        sessionAdapter.performExpand();
    }

    private void updateMenus() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);

        // assign logged in state
        SessionState sessionSourceProvider = (SessionState) service
            .getSourceProvider(SessionState.SESSION_STATE);
        sessionSourceProvider.setLoggedInState(sessionAdapter != null);

        // assign debug state
        DebugState debugStateSourceProvider = (DebugState) service
            .getSourceProvider(DebugState.SESSION_STATE);
        debugStateSourceProvider.setState(BioBankPlugin.getDefault()
            .isDebugging());
    }

    public SessionAdapter getSession() {
        Assert.isNotNull(sessionAdapter, "session adapter is null");
        return sessionAdapter;
    }

    public static WritableApplicationService getAppService() {
        return getInstance().getSession().getAppService();
    }

    public void updateTreeNode(AdapterBase node) {
        if (view != null) {
            view.getTreeViewer().update(node, null);
        }
    }

    public void setSelectedNode(AdapterBase node) {
        if (view != null) {
            view.getTreeViewer().setSelection(new StructuredSelection(node));
        }
    }

    public AdapterBase getSelectedNode() {
        if (view != null) {
            IStructuredSelection treeSelection = (IStructuredSelection) view
                .getTreeViewer().getSelection();
            if (treeSelection != null && treeSelection.size() > 0) {
                return (AdapterBase) treeSelection.getFirstElement();
            }
        }
        return null;
    }

    public void openViewForm(Class<?> klass, int id) {
        NodeSearchVisitor v = new NodeSearchVisitor(
            (Class<? extends ModelWrapper<?>>) klass, id);
        AdapterBase adapter = sessionAdapter.accept(v);
        Assert.isNotNull(adapter, "could not find adapter for class "
            + klass.getName() + " id " + id);
        adapter.performDoubleClick();
    }

    public void selectTreeNode(AdapterBase adapter) {
        SessionManager.getInstance().getTreeViewer().setSelection(
            new StructuredSelection(adapter));
    }

    public void openViewForm(ModelWrapper<?> wrapper) {
        openViewForm(wrapper.getClass(), wrapper.getId());
    }

    public AdapterBase searchNode(ModelWrapper<?> wrapper) {
        return sessionAdapter.searchChild(wrapper);
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    public SiteWrapper getCurrentSiteWrapper() {
        return siteManager.getCurrentSiteWrapper();
    }

    public TreeViewer getTreeViewer() {
        if (view != null)
            return view.getTreeViewer();
        return null;
    }

    public void setSiteManagerEnabled(boolean enable) {
        Assert.isNotNull(siteManager, "site manager is null");
        siteManager.setEnabled(enable);
    }

    public void setSiteCombo(SiteCombo siteCombo) {
        this.siteCombo = siteCombo;
    }

    public void updateSites() {
        Assert.isNotNull(siteManager, "site manager is null");
        siteManager.updateSites();
    }
}
