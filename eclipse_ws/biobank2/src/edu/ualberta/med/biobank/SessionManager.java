package edu.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.rcp.Application;
import edu.ualberta.med.biobank.rcp.SiteCombo;
import edu.ualberta.med.biobank.sourceproviders.DebugState;
import edu.ualberta.med.biobank.sourceproviders.SessionState;
import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.views.SessionsView;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionManager {

    private static final String SITE_PREF_NODE = "Site";

    private static final String LAST_SERVER_PREF = "lastServer";

    private static final String LAST_SITE_PREF = "lastSite";

    private static SessionManager instance = null;

    private static Logger log4j = Logger.getLogger(SessionManager.class
        .getName());

    private SessionsView view;

    private SessionAdapter sessionAdapter;

    private RootNode rootNode;

    private SiteWrapper currentSiteWrapper;
    private SiteCombo siteCombo;

    private List<SiteWrapper> currentSiteWrappers;

    private SessionManager() {
        super();
        rootNode = new RootNode();
        currentSiteWrappers = new ArrayList<SiteWrapper>();
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
        sessionAdapter = new SessionAdapter(rootNode, appService, 0,
            serverName, userName);
        rootNode.addChild(sessionAdapter);
        getCurrentSite(serverName, sites);
        updateSites(sites);
        sessionAdapter.loadChildren(true);
        siteCombo.setSession(sessionAdapter);
        if (view != null) {
            view.getTreeViewer().expandToLevel(3);
        }
        log4j.debug("addSession: " + serverName);
        updateMenus();
    }

    // selects the site the user was working with the last time he / she logged
    // out if logged into same server and same site exists
    private void getCurrentSite(String serverName, Collection<SiteWrapper> sites) {
        if (currentSiteWrapper != null)
            return;

        Preferences prefs = new InstanceScope().getNode(Application.PLUGIN_ID);
        Preferences prefNode = prefs.node(SITE_PREF_NODE);
        String lastServer = prefNode.get(LAST_SERVER_PREF, "");

        if (!lastServer.equals(serverName))
            return;

        String siteId = prefNode.get(LAST_SITE_PREF, "-1");

        if (siteId.equalsIgnoreCase("-1"))
            return;

        for (SiteWrapper site : sites) {
            if (site.getId().toString().equals(siteId))
                currentSiteWrapper = site;
        }
    }

    public void deleteSession() {
        setCurrentSite(null);
        rootNode.removeChild(sessionAdapter);
        sessionAdapter = null;
        updateMenus();
        currentSiteWrappers = new ArrayList<SiteWrapper>();
        siteCombo.setInput(currentSiteWrappers);
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

    public void setSiteCombo(SiteCombo combo) {
        this.siteCombo = combo;
    }

    public SiteCombo getSiteCombo() {
        return siteCombo;
    }

    public SessionAdapter getSession() {
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

    public static Logger getLogger() {
        return log4j;
    }

    public void openViewForm(Class<?> klass, int id) {
        NodeSearchVisitor v = new NodeSearchVisitor(klass, id);
        AdapterBase adapter = sessionAdapter.accept(v);
        Assert.isNotNull(adapter, "could not find adapter for class "
            + klass.getName() + " id " + id);
        adapter.performDoubleClick();
    }

    public void openViewForm(ModelWrapper<?> wrapper) {
        openViewForm(wrapper.getWrappedClass(), wrapper.getId());
    }

    public AdapterBase searchNode(ModelWrapper<?> wrapper) {
        return sessionAdapter.searchChild(wrapper);
    }

    public void setCurrentSite(SiteWrapper siteWrapper) {
        try {
            currentSiteWrapper = siteWrapper;
            String saveVal = "-1";
            if ((siteWrapper != null) && (siteWrapper.getId() != null))
                saveVal = siteWrapper.getId().toString();
            Preferences prefs = new InstanceScope()
                .getNode(Application.PLUGIN_ID);
            Preferences prefNode = prefs.node(SITE_PREF_NODE);
            prefNode.put(LAST_SERVER_PREF, sessionAdapter.getName());
            prefNode.put(LAST_SITE_PREF, saveVal);
            prefs.flush();
            IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
            ISourceProviderService service = (ISourceProviderService) window
                .getService(ISourceProviderService.class);
            SiteSelectionState siteSelectionStateSourceProvider = (SiteSelectionState) service
                .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
            siteSelectionStateSourceProvider.setSiteSelection(siteWrapper);
            if (sessionAdapter != null) {
                sessionAdapter.performExpand();
            }
        } catch (BackingStoreException e) {
            getLogger().error("Could not save site preferences", e);
        }
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    public SiteWrapper getCurrentSiteWrapper() {
        return currentSiteWrapper;
    }

    private void updateSites(Collection<SiteWrapper> sites) {
        currentSiteWrappers.clear();
        SiteWrapper allSiteWrapper = new SiteWrapper(getAppService(),
            new Site());
        allSiteWrapper.setName("All Sites");
        currentSiteWrappers.add(0, allSiteWrapper);
        for (SiteWrapper site : sites) {
            currentSiteWrappers.add(site);
        }
        siteCombo.setInput(currentSiteWrappers);
        if (currentSiteWrapper == null)
            siteCombo.setSelection(allSiteWrapper);
        else
            siteCombo.setSelection(currentSiteWrapper);
    }

    public void updateSites() {
        try {
            updateSites(ModelUtils.getSites(getAppService(), null));
        } catch (Exception e) {
            getLogger().error("Cannot update Sites", e);
        }
    }

    public TreeViewer getTreeViewer() {
        if (view != null)
            return view.getTreeViewer();
        return null;
    }
}
