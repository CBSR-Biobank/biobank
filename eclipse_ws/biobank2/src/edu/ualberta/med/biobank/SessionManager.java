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

    private Site currentSite;
    private SiteCombo siteCombo;

    private List<Site> currentSites;

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
        currentSites = new ArrayList<Site>();

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
            currentSite = null;
        else
            try {
                Site searchSite = new Site();
                searchSite.setId(Integer.valueOf(siteId));
                List<Site> query = getAppService().search(Site.class,
                    searchSite);
                if (query.size() == 1)
                    currentSite = query.get(0);
                else
                    currentSite = null;
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
        currentSites = new ArrayList<Site>();
        siteCombo.comboViewer.setInput(currentSites);
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

    public void setCurrentSite(Site site) {
        currentSite = site;
        String saveVal = "-1";
        if (site != null && site.getId() != null)
            saveVal = site.getId().toString();
        Preferences prefs = new InstanceScope().getNode(Application.PLUGIN_ID);
        prefs.node(SITE_PREF_NODE).put(LAST_SITE_PREF, saveVal);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            getLogger().error("Could not save site preferences", e);
        }
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        SiteSelectionState siteSelectionStateSourceProvider = (SiteSelectionState) service
            .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
        siteSelectionStateSourceProvider.setSiteSelection(site);
        if (sessionAdapter != null) {
            sessionAdapter.performExpand();
        }
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    public Site getCurrentSite() {
        return currentSite;
    }

    public List<Site> getCurrentSites() {
        return currentSites;
    }

    public void updateSites() {
        try {
            currentSites = getAppService().search(Site.class, new Site());
            Collections.sort(currentSites, new SiteComparator());
            Site allSite = new Site();
            allSite.setName("All Sites");
            currentSites.add(0, allSite);
            siteCombo.comboViewer.setInput(currentSites);
            if (currentSite == null)
                siteCombo.setSelection(allSite);
            else
                siteCombo.setSelection(currentSite);
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
