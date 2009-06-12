package edu.ualberta.med.biobank;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

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
