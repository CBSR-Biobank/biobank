package edu.ualberta.med.biobank;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.common.security.SecurityHelper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.rcp.MainPerspective;
import edu.ualberta.med.biobank.rcp.SiteCombo;
import edu.ualberta.med.biobank.sourceproviders.DebugState;
import edu.ualberta.med.biobank.sourceproviders.SessionState;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterFactory;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.views.AbstractViewWithTree;
import edu.ualberta.med.biobank.views.SessionsView;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionManager {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SessionManager.class.getName());

    private static SessionManager instance = null;

    private SessionsView view;

    private SessionAdapter sessionAdapter;

    private RootNode rootNode;

    private SiteCombo siteCombo;

    private SiteManager siteManager;

    public static HashMap<String, Integer> failedLoginAttempts = new HashMap<String, Integer>();

    /**
     * Map a perspective ID to a AbstractViewWithTree instance visible when the
     * perspective is set
     */
    public Map<String, AbstractViewWithTree> possibleViewMap;

    private SessionManager() {
        super();
        rootNode = new RootNode();
        possibleViewMap = new HashMap<String, AbstractViewWithTree>();
        siteManager = new SiteManager();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSessionsView(SessionsView view) {
        this.view = view;
        addView(MainPerspective.ID, view);
        updateMenus();
    }

    public void addSession(final WritableApplicationService appService,
        String serverName, String userName, Collection<SiteWrapper> sites) {
        logger.debug("addSession: " + serverName + ", user/" + userName
            + " numSites/" + sites.size());
        sessionAdapter = new SessionAdapter(rootNode, appService, 0,
            serverName, userName);
        rootNode.addChild(sessionAdapter);

        siteManager.init(appService, serverName);
        Assert.isNotNull(siteCombo, "site combo is null");
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

    public static void updateTreeNode(AdapterBase node) {
        AbstractViewWithTree view = getCurrentViewWithTree();
        if (view != null) {
            view.getTreeViewer().update(node, null);
        }
    }

    public static void refreshTreeNode(AdapterBase node) {
        AbstractViewWithTree view = getCurrentViewWithTree();
        if (view != null) {
            view.getTreeViewer().refresh(node, true);
        }
    }

    public static void setSelectedNode(AdapterBase node) {
        AbstractViewWithTree view = getCurrentViewWithTree();
        if (view != null && node != null) {
            view.setSelectedNode(node);
        }
    }

    public static AdapterBase getSelectedNode() {
        AbstractViewWithTree view = getCurrentViewWithTree();
        if (view != null) {
            AdapterBase selectedNode = view.getSelectedNode();
            return selectedNode;
        }
        return null;
    }

    public static void openViewForm(ModelWrapper<?> wrapper) {
        AdapterBase adapter = searchNode(wrapper);
        if (adapter != null) {
            adapter.performDoubleClick();
            return;
        }

        // adapter for object not yet in the tree, create new adapter
        // and load the view form
        adapter = AdapterFactory.getAdapter(wrapper);
        if (adapter != null) {
            adapter.performDoubleClick();
        }
    }

    public static AbstractViewWithTree getCurrentViewWithTree() {
        IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
        IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow()
            .getActivePage();
        return getInstance().possibleViewMap.get(activePage.getPerspective()
            .getId());
    }

    public static AdapterBase searchNode(ModelWrapper<?> wrapper) {
        AbstractViewWithTree view = getCurrentViewWithTree();
        if (view != null) {
            return view.searchNode(wrapper);
        }
        return null;
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    public SiteWrapper getCurrentSite() {
        return siteManager.getCurrentSite();
    }

    public void setSiteManagerEnabled(boolean enable) {
        Assert.isNotNull(siteManager, "site manager is null");
        siteManager.setEnabled(enable);
    }

    public void lockSite() {
        Assert.isNotNull(siteManager, "site manager is null");
        siteManager.lockSite();
    }

    public void unlockSite() {
        Assert.isNotNull(siteManager, "site manager is null");
        siteManager.unlockSite();
    }

    public boolean isAllSitesSelected() {
        Assert.isNotNull(siteManager, "site manager is null");
        return siteManager.isAllSitesSelected();
    }

    public void setSiteCombo(SiteCombo siteCombo) {
        this.siteCombo = siteCombo;
    }

    public SiteCombo getSiteCombo() {
        return this.siteCombo;
    }

    public void updateSites() {
        Assert.isNotNull(siteManager, "site manager is null");
        siteManager.updateSites();
    }

    public static void addView(String perspectiveId, AbstractViewWithTree view) {
        getInstance().possibleViewMap.put(perspectiveId, view);
    }

    public void rebuildSession() {
        SessionManager.getInstance().getSession().rebuild();
        if (view != null) {
            view.getTreeViewer().expandToLevel(3);
        }
    }

    public static String getUser() {
        return getInstance().getSession().getUserName();
    }

    public static boolean canCreate(Class<?> clazz) {
        return SecurityHelper.canCreate(getAppService(), clazz);
    }

    public static boolean canDelete(Class<?> clazz) {
        return SecurityHelper.canCreate(getAppService(), clazz);
    }

    public static boolean canView(Class<?> clazz) {
        return SecurityHelper.canView(getAppService(), clazz);
    }

    public static boolean canUpdate(Class<?> clazz) {
        return SecurityHelper.canUpdate(getAppService(), clazz);
    }

    public boolean isConnected() {
        return sessionAdapter != null;
    }
}
