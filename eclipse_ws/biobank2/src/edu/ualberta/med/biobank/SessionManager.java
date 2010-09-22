package edu.ualberta.med.biobank;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.security.Privilege;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.ChangePasswordDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.rcp.MainPerspective;
import edu.ualberta.med.biobank.rcp.SiteCombo;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.sourceproviders.DebugState;
import edu.ualberta.med.biobank.sourceproviders.SessionState;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterFactory;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.views.AbstractViewWithAdapterTree;
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

    /**
     * Map a perspective ID to a AbstractViewWithTree instance visible when the
     * perspective is set
     */
    public Map<String, AbstractViewWithAdapterTree> possibleViewMap;

    private SessionManager() {
        super();
        rootNode = new RootNode();
        possibleViewMap = new HashMap<String, AbstractViewWithAdapterTree>();
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

    public void addSession(final BiobankApplicationService appService,
        String serverName, User user, Collection<SiteWrapper> sites) {
        logger.debug("addSession: " + serverName + ", user/" + user.getLogin()
            + " numSites/" + sites.size());
        sessionAdapter = new SessionAdapter(rootNode, appService, 0,
            serverName, user);
        rootNode.addChild(sessionAdapter);

        siteManager.init(appService, serverName);
        Assert.isNotNull(siteCombo, "site combo is null");
        siteManager.setSiteCombo(siteCombo);
        siteManager.getCurrentSite(serverName, sites);
        siteManager.updateSites(sites);

        rebuildSession();
        updateMenus();

        if (sessionAdapter.getUser().isNeedToChangePassword()) {
            ChangePasswordDialog dlg = new ChangePasswordDialog(PlatformUI
                .getWorkbench().getActiveWorkbenchWindow().getShell(), true);
            dlg.open();
        }
    }

    public void deleteSession() throws Exception {
        WritableApplicationService appService = sessionAdapter.getAppService();
        siteManager.setEnabled(false);
        rootNode.removeChild(sessionAdapter);
        sessionAdapter = null;
        updateMenus();
        ServiceConnection.logout(appService);
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
            .getSourceProvider(SessionState.LOGIN_STATE_SOURCE_NAME);
        sessionSourceProvider.setLoggedInState(sessionAdapter != null);
        sessionSourceProvider.setWebAdmin(sessionAdapter != null
            && sessionAdapter.getUser().isWebsiteAdministrator());

        // assign debug state
        DebugState debugStateSourceProvider = (DebugState) service
            .getSourceProvider(DebugState.SESSION_STATE);
        debugStateSourceProvider.setState(BioBankPlugin.getDefault()
            .isDebugging());

        int menusize = window.getShell().getMenuBar().getItemCount();
        MenuItem help = window.getShell().getMenuBar().getItem(menusize - 1);
        MenuItem[] items = help.getMenu().getItems();
        for (MenuItem item : items) {
            item.setEnabled(sessionAdapter != null);
        }

    }

    public SessionAdapter getSession() {
        Assert.isNotNull(sessionAdapter, "session adapter is null");
        return sessionAdapter;
    }

    public static BiobankApplicationService getAppService() {
        return getInstance().getSession().getAppService();
    }

    public static void updateAdapterTreeNode(AdapterBase node) {
        AbstractViewWithAdapterTree view = getCurrentAdapterViewWithTree();
        if (view != null) {
            view.getTreeViewer().update(node, null);
        }
    }

    public static void refreshTreeNode(AdapterBase node) {
        AbstractViewWithAdapterTree view = getCurrentAdapterViewWithTree();
        if (view != null) {
            view.getTreeViewer().refresh(node, true);
        }
    }

    public static void setSelectedNode(AdapterBase node) {
        AbstractViewWithAdapterTree view = getCurrentAdapterViewWithTree();
        if (view != null && node != null) {
            view.setSelectedNode(node);
        }
    }

    public static AdapterBase getSelectedNode() {
        AbstractViewWithAdapterTree view = getCurrentAdapterViewWithTree();
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

    public static AbstractViewWithAdapterTree getCurrentAdapterViewWithTree() {
        IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
        IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow()
            .getActivePage();
        return getInstance().possibleViewMap.get(activePage.getPerspective()
            .getId());
    }

    public static AdapterBase searchNode(ModelWrapper<?> wrapper) {
        AbstractViewWithAdapterTree view = getCurrentAdapterViewWithTree();
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

    public static void addView(String perspectiveId,
        AbstractViewWithAdapterTree view) {
        getInstance().possibleViewMap.put(perspectiveId, view);
    }

    public void rebuildSession() {
        SessionAdapter session = SessionManager.getInstance().getSession();
        if (session != null) {
            session.rebuild();
        }
        if (view != null) {
            if (!isAllSitesSelected()) {
                SiteAdapter site = (SiteAdapter) getSession()
                    .getSitesGroupNode().search(getCurrentSite());
                if (site != null) {
                    site.performExpand();
                    return;
                }
            }
            // if allsite selected or can't find the site node
            getSession().getSitesGroupNode().performExpand();
        }
    }

    public static User getUser() {
        return getInstance().getSession().getUser();
    }

    public static String getServer() {
        return getInstance().getSession().getServerName();
    }

    public static boolean canCreate(Class<?> clazz) {
        return getUser().hasPrivilegeOnObject(Privilege.CREATE, clazz);
    }

    public static boolean canDelete(Class<?> clazz) {
        return getUser().hasPrivilegeOnObject(Privilege.DELETE, clazz);
    }

    public static boolean canView(Class<?> clazz) {
        return getUser().hasPrivilegeOnObject(Privilege.READ, clazz);
    }

    public static boolean canUpdate(Class<?> clazz) {
        return getUser().hasPrivilegeOnObject(Privilege.UPDATE, clazz);
    }

    public boolean isConnected() {
        return sessionAdapter != null;
    }

    public static void log(String action, String details, String type)
        throws Exception {
        ((BiobankApplicationService) getAppService()).logActivity(action,
            getInstance().getCurrentSite().getNameShort(), null, null, null,
            details, type);
    }
}
