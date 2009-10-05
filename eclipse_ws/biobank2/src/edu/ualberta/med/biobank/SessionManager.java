package edu.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SiteComparator;
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
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionManager {
    private static SessionManager instance = null;

    private static Logger log4j = Logger.getLogger(SessionManager.class
        .getName());

    private SessionsView view;

    private SessionAdapter sessionAdapter;

    private RootNode rootNode;

    public boolean inactiveTimeout = false;

    private final Semaphore timeoutSem = new Semaphore(100, true);

    final int TIME_OUT = 900000;

    private SiteWrapper currentSiteWrapper;
    private SiteCombo siteCombo;

    private List<SiteWrapper> currentSiteWrappers;

    private static final String SITE_PREF_NODE = "Site";

    private static final String LAST_SITE_PREF = "lastSite";

    final Runnable timeoutRunnable = new Runnable() {
        public void run() {
            try {
                timeoutSem.acquire();
                inactiveTimeout = true;
                // System.out
                // .println("startInactivityTimer_runnable: inactiveTimeout/"
                // + inactiveTimeout);

                boolean logout = BioBankPlugin.openConfirm("Inactive Timeout",
                    "The application has been inactive for "
                        + (TIME_OUT / 1000)
                        + " seconds.\n Do you want to log out?");

                if (logout) {
                    deleteSession();
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().closeAllEditors(true);
                }
                timeoutSem.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

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
        String name, String userName, List<Site> sites) {
        sessionAdapter = new SessionAdapter(rootNode, appService, 0, name,
            userName);
        rootNode.addChild(sessionAdapter);
        Collections.sort(sites, new SiteComparator());
        Preferences prefs = new InstanceScope().getNode(Application.PLUGIN_ID);
        Preferences lastSite = prefs.node(SITE_PREF_NODE);
        String siteId = lastSite.get(LAST_SITE_PREF, "-1");
        if (siteId.equalsIgnoreCase("-1"))
            currentSiteWrapper = null;
        else
            try {
                Site searchSite = new Site();
                searchSite.setId(Integer.valueOf(siteId));
                List<Site> query = getAppService().search(Site.class,
                    searchSite);
                if (query.size() == 1)
                    currentSiteWrapper = new SiteWrapper(appService, query
                        .get(0));
                else
                    currentSiteWrapper = null;
            } catch (ApplicationException e) {
                SessionManager.getLogger().error(
                    "Error retrieving site " + siteId, e);
            }
        updateSites();
        sessionAdapter.loadChildren(true);
        siteCombo.setSession(sessionAdapter);
        if (view != null) {
            view.getTreeViewer().expandToLevel(3);
        }
        log4j.debug("addSession: " + name);
        startInactivityTimer();
        updateMenus();

    }

    private void startInactivityTimer() {
        try {
            timeoutSem.acquire();
            inactiveTimeout = false;
            // System.out.println("startInactivityTimer: inactiveTimeout/"
            // + inactiveTimeout);

            final Display display = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell().getDisplay();

            // this listener will be called when the events listed below happen
            Listener idleListener = new Listener() {
                public void handleEvent(Event event) {
                    try {
                        timeoutSem.acquire();
                        // System.out
                        // .println("startInactivityTimer_idleListener: inactiveTimeout/"
                        // + inactiveTimeout);
                        if (!inactiveTimeout && (sessionAdapter != null)) {
                            display.timerExec(TIME_OUT, timeoutRunnable);
                        }
                        timeoutSem.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            int[] events = { SWT.KeyDown, SWT.KeyUp, SWT.MouseDown,
                SWT.MouseMove, SWT.MouseUp };
            for (int event : events) {
                display.addFilter(event, idleListener);
            }
            display.timerExec(TIME_OUT, timeoutRunnable);
            timeoutSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteSession() {
        rootNode.removeChild(sessionAdapter);
        sessionAdapter = null;
        updateMenus();
        currentSiteWrappers = new ArrayList<SiteWrapper>();
        siteCombo.comboViewer.setInput(currentSiteWrappers);
        setCurrentSite(null);
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
            prefs.node(SITE_PREF_NODE).put(LAST_SITE_PREF, saveVal);
            prefs.flush();
            IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
            ISourceProviderService service = (ISourceProviderService) window
                .getService(ISourceProviderService.class);
            SiteSelectionState siteSelectionStateSourceProvider = (SiteSelectionState) service
                .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
            Site site = null;
            if (siteWrapper != null)
                site = siteWrapper.getWrappedObject();
            siteSelectionStateSourceProvider.setSiteSelection(site);
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

    public List<SiteWrapper> getCurrentSiteWrappers() {
        return currentSiteWrappers;
    }

    public void updateSites() {
        try {
            List<Site> sites = getAppService().search(Site.class, new Site());
            currentSiteWrappers.clear();
            for (Site site : sites) {
                currentSiteWrappers.add(new SiteWrapper(getAppService(), site));
            }
            Collections.sort(currentSiteWrappers);
            SiteWrapper allSiteWrapper = new SiteWrapper(getAppService(),
                new Site());
            allSiteWrapper.setName("All Sites");
            currentSiteWrappers.add(0, allSiteWrapper);
            siteCombo.comboViewer.setInput(currentSiteWrappers);
            if (currentSiteWrapper == null)
                siteCombo.setSelection(allSiteWrapper);
            else
                siteCombo.setSelection(currentSiteWrapper);
        } catch (ApplicationException e) {
            getLogger().error("Cannot update Sites", e);
        }
    }

    public TreeViewer getTreeViewer() {
        if (view != null)
            return view.getTreeViewer();
        return null;
    }
}
