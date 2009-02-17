package edu.ualberta.med.biobank;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.ui.PartInitException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.forms.NodeInput;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.views.SessionsView;

public class SessionManager {
	private static SessionManager instance = null;
	
	private SessionsView view;
	
	private HashMap<String, SessionAdapter> sessions;
	
	private Node rootNode;
	
	public Node getRootNode() {
		return rootNode;
	}

	private IDoubleClickListener doubleClickListener = new IDoubleClickListener() {
		public void doubleClick(DoubleClickEvent event) {
			Object selection = event.getSelection();

			if (selection == null) return;

			Object element = ((StructuredSelection)selection).getFirstElement();

			view.getTreeViewer().expandToLevel(element, 1);

			if (element instanceof SiteAdapter) {
				openSiteNode((SiteAdapter) element);
			}
			else if (element instanceof ClinicAdapter) {
				openClinicNode((ClinicAdapter) element);
			}
			else if (element instanceof Node) {
				Node node = (Node) element;
				if (node.getName().equals("Clinics")) {
					updateClinics(node);
				}
			}
			else {
				Assert.isTrue(false, "double click on class "
						+ element.getClass().getName() + " not implemented yet");
			}
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
			Object o = e.getElement();
			if (o instanceof Node) {
				Node node = (Node) o;
				if (node.getName().equals("Clinics")) {
					updateClinics(node);
				}			
			}
		}
	};
	
	public ITreeViewerListener getTreeViewerListener() {
		return treeViewerListener;
	}

	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}
	
	private SessionManager() {
		super();
		rootNode = new Node(null, 1, "root");
		sessions = new  HashMap<String, SessionAdapter>();
	}
	
	public void setSessionsView(SessionsView view) {
		this.view = view;
	}
	
	public void addSession(final WritableApplicationService appService, String name, 
			List<Site> sites) {
		int id = sessions.size();
		final SessionAdapter sessionNode = new SessionAdapter(rootNode, appService, id, name);
		sessions.put(name, sessionNode);
		rootNode.addChild(sessionNode);
		
		for (Object o : sites) {
			SiteAdapter siteNode = new SiteAdapter(sessionNode, (Site) o);
			sessionNode.addChild(siteNode);
		}
		view.getTreeViewer().expandToLevel(2);
	}
	
	public SessionAdapter getSessionAdapter(String sessionName) {
		for (Node node : rootNode.getChildren()) {
			if (node.getName().equals(sessionName)) 
				return (SessionAdapter) node;
		}
		Assert.isTrue(false, "Session with name " + sessionName
				+ " not found");
		return null;
	}
	
	public SessionAdapter getSessionAdapter(int count) {
		List<Node> nodes = rootNode.getChildren();
		Assert.isTrue(count < nodes.size(), 
				"Invalid session node count: " + count);
		return (SessionAdapter) nodes.get(count);
	}
	
	public void updateClinics(final Node groupNode) {		
		final Site site = ((SiteAdapter) groupNode.getParent()).getSite();
		Assert.isNotNull(site, "null site");
		
		view.getTreeViewer().getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				site.getClinicCollection();
				
				for (Clinic clinic : site.getClinicCollection()) {
					ClinicAdapter node = new ClinicAdapter(groupNode, clinic);
					groupNode.addChild(node);
				}
				
				view.getTreeViewer().expandToLevel(groupNode, 1);
			}
		});
	}
	
	public void deleteSession(String name) {
		rootNode.removeByName(name);
		//treeViewer.refresh();
	}
	
	public int getSessionCount() {
		return rootNode.getChildren().size();
	}
	
	public String[] getSessionNames() {
		return sessions.keySet().toArray(new String[sessions.size()]);
	}
	
	private void openSiteNode(SiteAdapter node) {
		NodeInput input = new NodeInput(node);
		
		try {
			view.getSite().getPage().openEditor(input, SiteViewForm.ID, true);
		} 
		catch (PartInitException e) {
			// handle error
			e.printStackTrace();				
		}
	}
	
	private void openClinicNode(ClinicAdapter node) {
		NodeInput input = new NodeInput(node);
		
		try {
			view.getSite().getPage().openEditor(input, ClinicViewForm.ID, true);
		} 
		catch (PartInitException e) {
			// handle error
			e.printStackTrace();				
		}
	}
	
	public SessionAdapter getSessionSingle() {
		int count = sessions.size();
		Assert.isTrue(count == 1, "No sessions or more than 1 session connected");
		return getSessionAdapter(0);
	}
}
