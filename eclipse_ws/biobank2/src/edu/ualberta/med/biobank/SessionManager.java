package edu.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.BgcSessionState;
import edu.ualberta.med.biobank.rcp.perspective.MainPerspective;
import edu.ualberta.med.biobank.rcp.perspective.PerspectiveSecurity;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.sourceproviders.DebugState;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import edu.ualberta.med.biobank.treeview.util.AdapterFactory;
import edu.ualberta.med.biobank.utils.BindingContextHelper;
import edu.ualberta.med.biobank.views.AbstractViewWithAdapterTree;
import edu.ualberta.med.biobank.views.SessionsView;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionManager {

    public static final String BIOBANK2_CONTEXT_LOGGED_OUT =
        "biobank.context.loggedOut"; //$NON-NLS-1$

    public static final String BIOBANK2_CONTEXT_LOGGED_IN =
        "biobank.context.loggedIn"; //$NON-NLS-1$

    private static BgcLogger logger = BgcLogger.getLogger(SessionManager.class
        .getName());

    private static SessionManager instance = null;

    private SessionAdapter sessionAdapter;

    private RootNode rootNode;

    /**
     * Map a perspective ID to a AbstractViewWithTree instance visible when the
     * perspective is set
     */
    public Map<String, AbstractViewWithAdapterTree> possibleViewMap;

    private String currentAdministrationViewId;

    private Map<String, Boolean> perspectivesUpdateDone;

    private SessionManager() {
        super();
        rootNode = new RootNode();
        possibleViewMap = new HashMap<String, AbstractViewWithAdapterTree>();
        initPerspectivesUpdateDone();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSessionsView(SessionsView view) {
        addView(view);
        updateSessionState();
    }

    public void addSession(final BiobankApplicationService appService,
        String serverName, UserWrapper user) {
        logger.debug("addSession: " + serverName + ", user/" + user.getLogin()); //$NON-NLS-1$ //$NON-NLS-2$
        sessionAdapter = new SessionAdapter(rootNode, appService, 0,
            serverName, user);
        rootNode.addChild(sessionAdapter);
        updateSessionState();

        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
            .getActivePage();
        updateViewsVisibility(page, true);
    }

    public void deleteSession() throws Exception {
        WritableApplicationService appService = sessionAdapter.getAppService();
        sessionAdapter = null;
        updateSessionState();
        ServiceConnection.logout(appService);
        initPerspectivesUpdateDone();
    }

    private void initPerspectivesUpdateDone() {
        if (perspectivesUpdateDone == null)
            perspectivesUpdateDone = new HashMap<String, Boolean>();
        perspectivesUpdateDone.clear();
    }

    public void updateSession() {
        Assert.isNotNull(sessionAdapter, "session adapter is null"); //$NON-NLS-1$
        sessionAdapter.performExpand();
    }

    private void updateSessionState() {
        // for key binding contexts:
        if (sessionAdapter == null) {
            BindingContextHelper
                .activateContextInWorkbench(BIOBANK2_CONTEXT_LOGGED_OUT);
            BindingContextHelper
                .deactivateContextInWorkbench(BIOBANK2_CONTEXT_LOGGED_IN);
        } else {
            BindingContextHelper
                .activateContextInWorkbench(BIOBANK2_CONTEXT_LOGGED_IN);
            BindingContextHelper
                .deactivateContextInWorkbench(BIOBANK2_CONTEXT_LOGGED_OUT);
        }

        // assign logged in state
        BgcSessionState guiCommonSessionState = BgcPlugin
            .getSessionStateSourceProvider();
        guiCommonSessionState.setLoggedInState(sessionAdapter != null);

        BiobankPlugin.getSessionStateSourceProvider().setUser(
            sessionAdapter == null ? null : sessionAdapter.getUser());

        // assign debug state
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);

        DebugState debugStateSourceProvider = (DebugState) service
            .getSourceProvider(DebugState.SESSION_STATE);
        debugStateSourceProvider.setState(BiobankPlugin.getDefault()
            .isDebugging());
    }

    public SessionAdapter getSession() {
        Assert.isNotNull(sessionAdapter,
            Messages.SessionManager_noconnection_error_msg);
        return sessionAdapter;
    }

    public static BiobankApplicationService getAppService() {
        return getInstance().getSession().getAppService();
    }

    public static void updateAdapterTreeNode(final AbstractAdapterBase node) {
        final AbstractViewWithAdapterTree view =
            getCurrentAdapterViewWithTree();
        if ((view != null) && (node != null)) {
            view.getTreeViewer().update(node, null);
        }
    }

    public static void refreshTreeNode(final AbstractAdapterBase node) {
        final AbstractViewWithAdapterTree view =
            getCurrentAdapterViewWithTree();
        if (view != null && !view.getTreeViewer().getControl().isDisposed()) {
            view.getTreeViewer().refresh(node, true);
        }
    }

    public static AbstractAdapterBase getSelectedNode() {
        AbstractViewWithAdapterTree view = getCurrentAdapterViewWithTree();
        if (view != null) {
            AbstractAdapterBase selectedNode = view.getSelectedNode();
            return selectedNode;
        }
        return null;
    }

    public static void openViewForm(ModelWrapper<?> wrapper) {
        AbstractAdapterBase adapter = searchFirstNode(wrapper.getClass(),
            wrapper.getId());
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
        SessionManager sm = getInstance();
        return sm.possibleViewMap.get(sm.currentAdministrationViewId);
    }

    public static List<AbstractAdapterBase> searchNodes(Class<?> searchedClass,
        Integer objectId) {
        AbstractViewWithAdapterTree view = getCurrentAdapterViewWithTree();
        if (view != null) {
            return view.searchNode(searchedClass, objectId);
        }
        return new ArrayList<AbstractAdapterBase>();
    }

    public static AbstractAdapterBase searchFirstNode(Class<?> searchedClass,
        Integer objectId) {
        List<AbstractAdapterBase> nodes = searchNodes(searchedClass, objectId);
        if (nodes.size() > 0) {
            return nodes.get(0);
        }
        return null;
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    public static void addView(AbstractViewWithAdapterTree view) {
        getInstance().possibleViewMap.put(view.getId(), view);
        getInstance().currentAdministrationViewId = view.getId();
    }

    public static UserWrapper getUser() {
        return getInstance().getSession().getUser();
    }

    public static String getServer() {
        return getInstance().getSession().getServerName();
    }

    public static boolean canCreate(Class<?> clazz) {
        return SessionSecurityHelper.canCreate(getAppService(), getUser(),
            clazz);
    }

    public static boolean canDelete(Class<?> clazz) {
        return SessionSecurityHelper.canDelete(getAppService(), getUser(),
            clazz);
    }

    public static boolean canDelete(ModelWrapper<?> wrapper) {
        return SessionSecurityHelper.canDelete(getUser(), wrapper);
    }

    public static boolean canView(Class<?> clazz) {
        try {
            return SessionSecurityHelper.canView(getAppService(), getUser(),
                clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // FIXME is using current working center and no study
    public static boolean isAllowed(String... keyDesc) {
        try {
            return SessionSecurityHelper.isAllowed(getAppService(), getUser(),
                keyDesc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAllowedorCanRead(String... keyDesc) {
        try {
            return SessionSecurityHelper.isAllowedorCanRead(getAppService(),
                getUser(), keyDesc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canUpdate(Class<?> clazz) {
        try {
            return SessionSecurityHelper.canUpdate(getAppService(), getUser(),
                clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canUpdate(ModelWrapper<?> wrapper) {
        return SessionSecurityHelper.canUpdate(getUser(), wrapper);
    }

    public boolean isConnected() {
        return sessionAdapter != null;
    }

    public static void log(String action, String details, String type)
        throws Exception {
        getAppService().logActivity(action, null, null, null, null, details,
            type);
    }

    public static void logLookup(ModelWrapper<?> wrapper) throws Exception {
        if (!wrapper.isNew())
            wrapper.logLookup(getUser().getCurrentWorkingCenter() == null ? "" //$NON-NLS-1$
                : getUser().getCurrentWorkingCenter().getNameShort());
    }

    public static void logEdit(ModelWrapper<?> wrapper) throws Exception {
        if (!wrapper.isNew())
            wrapper.logEdit(getUser().getCurrentWorkingCenter() == null ? "" //$NON-NLS-1$
                : getUser().getCurrentWorkingCenter().getNameShort());
    }

    /**
     * do an update on node holding the same wrapper than the given adapter.
     * 
     * @param canRest if true, then the node found will be reloaded from the
     *            database (might not want that if the object could be open in
     *            an entry form).
     * @param expandParent if true will expand the parent node of 'adapter'
     * 
     */
    public static void updateAllSimilarNodes(final AbstractAdapterBase adapter,
        final boolean canReset) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    // add to the correct node if it is a new adapter:
                    AbstractAdapterBase parent = adapter.getParent();
                    if (parent != null)
                        parent.addChild(adapter);
                    List<AbstractAdapterBase> res = searchNodes(
                        adapter.getClass(), adapter.getId());
                    final AbstractViewWithAdapterTree view =
                        getCurrentAdapterViewWithTree();
                    if (view != null) {
                        for (AbstractAdapterBase ab : res) {
                            if (canReset)
                                try {
                                    if (ab != adapter) {
                                        if (ab instanceof AdapterBase)
                                            ((AdapterBase) ab).resetObject();
                                    }
                                } catch (Exception ex) {
                                    logger.error("Problem reseting object", ex); //$NON-NLS-1$
                                }
                            view.getTreeViewer().update(ab, null);
                        }
                    }
                } catch (Exception ex) {
                    logger.error("Error updating tree nodes", ex); //$NON-NLS-1$
                }
            }
        });
    }

    public static void setCurrentAdministrationViewId(String id) {
        getInstance().currentAdministrationViewId = id;
    }

    public static boolean isSuperAdminMode() {
        return getUser().isInSuperAdminMode();
    }

    public static void updateViewsVisibility(final IWorkbenchPage page,
        final boolean login) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                try {
                    SessionManager sm = getInstance();
                    if (sm.isConnected()) {
                        String perspectiveId = page.getPerspective().getId();
                        Boolean done = sm.perspectivesUpdateDone
                            .get(perspectiveId);
                        if (done == null || !done) {
                            PerspectiveSecurity.updateVisibility(getUser(),
                                page);
                            sm.perspectivesUpdateDone.put(perspectiveId, true);
                        }
                    }
                } catch (PartInitException e) {
                    BgcPlugin.openAsyncError(
                        Messages.SessionManager_actions_error_title, e);
                }
                // don't want to switch if was activated by an handler after
                // login
                // (display is weird otherwise)
                if (login && page.getViewReferences().length == 0)
                    try {
                        page.getWorkbenchWindow()
                            .getWorkbench()
                            .showPerspective(MainPerspective.ID,
                                page.getWorkbenchWindow());
                    } catch (WorkbenchException e) {
                        logger.error("Error opening main perspective", e); //$NON-NLS-1$
                }
            }
        });
    }

}
