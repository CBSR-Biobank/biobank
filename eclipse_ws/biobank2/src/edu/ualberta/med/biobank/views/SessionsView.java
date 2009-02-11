package edu.ualberta.med.biobank.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionCredentials;
import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.forms.NodeInput;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.NodeContentProvider;
import edu.ualberta.med.biobank.treeview.NodeLabelProvider;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class SessionsView extends ViewPart {
	public static final String ID =
	      "edu.ualberta.med.biobank.session.SessionView";

	private TreeViewer treeViewer;
	
	private Node rootNode;
	
	private HashMap<String, SessionAdapter> sessions;
	
	private IDoubleClickListener doubleClickListener = new IDoubleClickListener() {
		public void doubleClick(DoubleClickEvent event) {
			Object selection = event.getSelection();

			if (selection == null) return;

			Object element = ((StructuredSelection)selection).getFirstElement();

			treeViewer.expandToLevel(element, 1);

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
	
	public SessionsView() {
		super();
		BioBankPlugin.getDefault().setSessionView(this);
		rootNode = new Node(null, 1, "root");
		sessions = new  HashMap<String, SessionAdapter>();
	}

	@Override
	public void createPartControl(Composite parent) {		
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		treeViewer.setLabelProvider(new NodeLabelProvider());
		treeViewer.setContentProvider(new NodeContentProvider());
        treeViewer.addDoubleClickListener(doubleClickListener);
        treeViewer.addTreeListener(treeViewerListener);
		treeViewer.setInput(rootNode);
	}
	
	@Override
	public void setFocus() {
	}
	
	public void createSession(final SessionCredentials sc) {
		Job job = new Job("logging in") {
			protected IStatus run(IProgressMonitor monitor) {
				
				monitor.beginTask("Logging in ... ", 100);					
				try {
					final WritableApplicationService appService;
					final String userName = sc.getUserName(); 
					final String url = "http://" + sc.getServer() + "/biobank2";
					
					if (userName.length() == 0) {
						appService =  (WritableApplicationService) 
						ApplicationServiceProvider.getApplicationServiceFromUrl(url);
					}
					else {
						appService = (WritableApplicationService) 
						ApplicationServiceProvider.getApplicationServiceFromUrl(url, userName, sc.getPassword());
					}	

					Site site = new Site();		
					final List<Object> sites = appService.search(Site.class, site);
					
					Display.getDefault().asyncExec(new Runnable() {
				          public void run() {
				        	  addSession(appService, sc.getServer(), sites);
				          }
					});
				}
				catch (final RemoteConnectFailureException exp) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
									"Connection Attempt Failed", 
									"Could not connect to server. Make sure server is running.");
						}
					});
				}
				catch (final Exception exp) {	
					exp.printStackTrace();
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
									"Login Failed", exp.getMessage());
						}
					});
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.schedule();
	}
	
	public void addSession(final WritableApplicationService appService, String name, 
			List<Object> sites) {
		int id = sessions.size();
		final SessionAdapter sessionNode = new SessionAdapter(rootNode, appService, id, name);
		sessions.put(name, sessionNode);
		rootNode.addChild(sessionNode);
		
		for (Object o : sites) {
			SiteAdapter siteNode = new SiteAdapter(sessionNode, (Site) o);
			sessionNode.addChild(siteNode);
		}
		treeViewer.refresh();
		treeViewer.expandToLevel(2);
	}
	
	public SessionAdapter getSessionNode(String sessionName) {
		for (Node node : rootNode.getChildren()) {
			if (node.getName().equals(sessionName)) 
				return (SessionAdapter) node;
		}
		Assert.isTrue(false, "Session with name " + sessionName
				+ " not found");
		return null;
	}
	
	public SessionAdapter getSessionNode(int count) {
		List<Node> nodes = rootNode.getChildren();
		Assert.isTrue(count < nodes.size(), 
				"Invalid session node count: " + count);
		return (SessionAdapter) nodes.get(count);
	}
	
	public void updateSites(final SessionAdapter sessionNode) {
		String sessionName = sessionNode.getName();
		Assert.isTrue(sessions.containsKey(sessionName), 
				"Session named " + sessionName + " not found");
		
		// get the Site sites stored on this server
		Job job = new Job("Querying Sites") {
			protected IStatus run(IProgressMonitor monitor) {
				
				monitor.beginTask("Querying Sites ... ", 100);
				
				Site site = new Site();				
				try {
					WritableApplicationService appService = sessionNode.getAppService();
					final List<Object> sites = appService.search(Site.class, site);
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							for (Object o : sites) {
								SiteAdapter siteNode 
									= new SiteAdapter(sessionNode, (Site) o);
								sessionNode.addChild(siteNode);
							}
							treeViewer.refresh(sessionNode);
						}
					});
				}
				catch (final RemoteConnectFailureException exp) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
									"Connection Attempt Failed", 
									"Could not connect to server. Make sure server is running.");
						}
					});
				}
				catch (Exception exp) {
					exp.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.schedule();
	}
	
	public void updateClinics(final Node groupNode) {
		final SiteAdapter siteNode = (SiteAdapter) groupNode.getParent();
		final SessionAdapter sessionNode = (SessionAdapter) siteNode.getParent();
		
		String sessionName = sessionNode.getName();
		Assert.isTrue(sessions.containsKey(sessionName), 
				"Session named " + sessionName + " not found");
		
		// get the Site sites stored on this server
		Job job = new Job("Querying Clinics") {
			protected IStatus run(IProgressMonitor monitor) {
				
				monitor.beginTask("Querying Clinics ... ", 100);
				
				try {
					Site site = siteNode.getSite();
					final Collection<Clinic> clinics = site.getClinicCollection();
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							for (Clinic clinic : clinics) {
								ClinicAdapter node = 
									new ClinicAdapter(groupNode, clinic);
								groupNode.addChild(node);
							}
							treeViewer.expandToLevel(groupNode, 1);
							treeViewer.refresh(groupNode);
						}
					});
				}
				catch (final RemoteConnectFailureException exp) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
									"Connection Attempt Failed", 
									"Could not connect to server. Make sure server is running.");
						}
					});
				}
				catch (Exception exp) {
					exp.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.schedule();
	}
	
	public void createObject(final String sessionName, final Object o) throws Exception {
		if (!sessions.containsKey(sessionName)) {
			throw new Exception();
		}
		
		final SessionAdapter sessionNode = sessions.get(sessionName);
		
		Job job = new Job("Creating Object") {
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Submitting information ... ", 100);
				
				try {
					SDKQuery query;
					SDKQueryResult result;
					WritableApplicationService appService = sessionNode.getAppService();
					
					if (o instanceof Site) {
						Site site = (Site) o;
						Assert.isTrue(site.getId() == null, "insert invoked on site already in database");
						Assert.isTrue(site.getAddress().getId() == null, "insert invoked on address already in database");
						
						query = new InsertExampleQuery(site.getAddress());					
						result = appService.executeQuery(query);
						site.setAddress((Address) result.getObjectResult());
						query = new InsertExampleQuery(site);	
						appService.executeQuery(query);
					}					
					else if (o instanceof Clinic) {
						Clinic clinic = (Clinic) o;
						Assert.isTrue(clinic.getId() == null, "insert invoked on site already in database");
						Assert.isTrue(clinic.getAddress().getId() == null, "insert invoked on address already in database");
						
						query = new InsertExampleQuery(clinic.getAddress());					
						result = appService.executeQuery(query);
						clinic.setAddress((Address) result.getObjectResult());
						query = new InsertExampleQuery(clinic);	
						appService.executeQuery(query);
					}
					else {
						Assert.isTrue(false, "creating of objects of type " 
								+ o.getClass().getName() + " not supported yet");
					}
					
					updateSites(sessionNode);
				}
				catch (final RemoteAccessException exp) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
									"Connection Attempt Failed", 
									"Could not perform database operation. Make sure server is running correct version.");
						}
					});
				}
				catch (Exception exp) {
					exp.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.schedule();
	}
	
	public void updateObject(final String sessionName, final Object o) throws Exception {
		Assert.isTrue(sessions.containsKey(sessionName), "Session named " + sessionName + " not found");
		
		final SessionAdapter sessionNode = sessions.get(sessionName);
		
		Job job = new Job("Creating Object") {
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Submitting information ... ", 100);
				
				try {
					SDKQuery query;
					SDKQueryResult result = null;
					WritableApplicationService appService = sessionNode.getAppService();
					
					if (o instanceof Site) {
						Site site = (Site) o;
						Assert.isNotNull(site.getId(), "update invoked on site not in database");
						Assert.isNotNull(site.getAddress().getId(), "update invoked on address not in database");
						
						query = new UpdateExampleQuery(site.getAddress());					
						result = appService.executeQuery(query);
						site.setAddress((Address) result.getObjectResult());
						query = new UpdateExampleQuery(site);	
						result = appService.executeQuery(query);
						
						updateSites(sessionNode);
						
						if (result != null) {
							final int id =  ((Site) result.getObjectResult()).getId();
							
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									openSiteNode((SiteAdapter) sessionNode.getChild(id));
								}
							});
						}
					}					
					else if (o instanceof Clinic) {
						Clinic clinic = (Clinic) o;
						Assert.isTrue(clinic.getId() == null, "insert invoked on site already in database");
						Assert.isTrue(clinic.getAddress().getId() == null, "insert invoked on address already in database");
						
						query = new UpdateExampleQuery(clinic.getAddress());					
						result = appService.executeQuery(query);
						clinic.setAddress((Address) result.getObjectResult());
						query = new UpdateExampleQuery(clinic);	
						appService.executeQuery(query);
					}
					else {
						Assert.isTrue(false, "updating of objects of type " 
								+ o.getClass().getName() + " not supported yet");
					}
				}
				catch (final RemoteConnectFailureException exp) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
									"Connection Attempt Failed",
									"Could not perform database operation. Make sure server is running correct version.");
						}
					});
				}
				catch (Exception exp) {
					exp.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.schedule();
	}
	
	public void deleteSession(String name) throws Exception {
		rootNode.removeByName(name);
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
			getSite().getPage().openEditor(input, SiteViewForm.ID, true);
		} 
		catch (PartInitException e) {
			// handle error
			e.printStackTrace();				
		}
	}
	
	private void openClinicNode(ClinicAdapter node) {
		NodeInput input = new NodeInput(node);
		
		try {
			getSite().getPage().openEditor(input, ClinicViewForm.ID, true);
		} 
		catch (PartInitException e) {
			// handle error
			e.printStackTrace();				
		}
	}
}
