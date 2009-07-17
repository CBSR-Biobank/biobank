package edu.ualberta.med.biobank;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.views.SessionsView;
import edu.ualberta.med.biobank.views.TreeFilter;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SessionManager {
    private static SessionManager instance = null;

    private static Logger log4j = Logger.getLogger(SessionManager.class
        .getName());

    private SessionsView view;

    private HashMap<String, SessionAdapter> sessionsByName;

    private Node rootNode;

    public boolean inactiveTimeout = false;

    private final Semaphore timeoutSem = new Semaphore(100, true);

    final int TIME_OUT = 900000;

    final Runnable timeoutRunnable = new Runnable() {
        public void run() {
            try {
                timeoutSem.acquire();
                inactiveTimeout = true;
                System.out
                    .println("startInactivityTimer_runnable: inactiveTimeout/"
                        + inactiveTimeout);

                boolean logout = BioBankPlugin.openConfirm("Inactive Timeout",
                    "The application has been inactive for "
                        + (TIME_OUT / 1000)
                        + " seconds.\n Do you want to log out?");

                if (logout) {
                    for (SessionAdapter adapter : sessionsByName.values()) {
                        deleteSession(adapter.getName());
                    }
                }
                timeoutSem.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public Node getRootNode() {
        return rootNode;
    }

    private IDoubleClickListener doubleClickListener = new IDoubleClickListener() {
        public void doubleClick(DoubleClickEvent event) {
            Object selection = event.getSelection();

            if (selection == null)
                return;

            Object element = ((StructuredSelection) selection)
                .getFirstElement();
            ((Node) element).performDoubleClick();
            view.getTreeViewer().expandToLevel(element, 1);
        }
    };

    public IDoubleClickListener getDoubleClickListener() {
        return doubleClickListener;
    }

    private ITreeViewerListener treeViewerListener = new ITreeViewerListener() {
        @Override
        public void treeCollapsed(TreeExpansionEvent e) {
        }

        @Override
        public void treeExpanded(TreeExpansionEvent e) {
            ((Node) e.getElement()).performExpand();
        }
    };

    /*
     * Pop-up menu for the tree viewer.
     */
    private Listener treeViewMenuListener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            TreeViewer tv = view.getTreeViewer();
            Tree tree = tv.getTree();
            Menu menu = tree.getMenu();

            for (MenuItem menuItem : menu.getItems()) {
                menuItem.dispose();
            }

            Object element = ((StructuredSelection) tv.getSelection())
                .getFirstElement();
            if (element != null) {
                ((Node) element).popupMenu(tv, tree, menu);
            }
        }
    };

    private SessionManager() {
        super();
        rootNode = RootNode.getRootNode();
        sessionsByName = new HashMap<String, SessionAdapter>();
    }

    public ITreeViewerListener getTreeViewerListener() {
        return treeViewerListener;
    }

    public Listener getTreeViewerMenuListener() {
        return treeViewMenuListener;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSessionsView(SessionsView view) {
        this.view = view;
    }

    public void addSession(final WritableApplicationService appService,
        String name, List<Site> sites) {
        int id = sessionsByName.size();
        final SessionAdapter sessionNode = new SessionAdapter(rootNode,
            appService, id, name);
        sessionsByName.put(name, sessionNode);
        rootNode.addChild(sessionNode);

        for (Object o : sites) {
            Site site = (Site) o;
            SiteAdapter siteNode = new SiteAdapter(sessionNode, site);
            sessionNode.addChild(siteNode);
        }
        view.getTreeViewer().expandToLevel(2);
        log4j.debug("addSession: " + name);
        startInactivityTimer();
    }

    private void startInactivityTimer() {
        try {
            timeoutSem.acquire();
            inactiveTimeout = false;
            System.out.println("startInactivityTimer: inactiveTimeout/"
                + inactiveTimeout);

            final Display display = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell().getDisplay();

            // this listener will be called when the events listed below happen
            Listener idleListener = new Listener() {
                public void handleEvent(Event event) {
                    try {
                        timeoutSem.acquire();
                        System.out
                            .println("startInactivityTimer_idleListener: inactiveTimeout/"
                                + inactiveTimeout);
                        if (!inactiveTimeout && (sessionsByName.size() > 0)) {
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

    public SessionAdapter getSessionAdapter(int count) {
        List<Node> nodes = rootNode.getChildren();
        Assert.isTrue(count < nodes.size(), "Invalid session node count: "
            + count);
        return (SessionAdapter) nodes.get(count);
    }

    public void deleteSession(String name) {
        rootNode.removeByName(name);
        // treeViewer.refresh();
    }

    public int getSessionCount() {
        return rootNode.getChildren().size();
    }

    public String[] getSessionNames() {
        return sessionsByName.keySet().toArray(
            new String[sessionsByName.size()]);
    }

    public SessionAdapter getSessionSingle() {
        int count = sessionsByName.size();
        Assert.isTrue(count == 1,
            "No sessions or more than 1 session connected");
        return getSessionAdapter(0);
    }

    public TreeViewer getTreeViewer() {
        return view.getTreeViewer();
    }

    public TreeFilter getTreeFilter() {
        return view.getFilter();
    }

    public static Logger getLogger() {
        return log4j;
    }
}
