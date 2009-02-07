package edu.ualberta.med.biobank.session;

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
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionCredentials;
import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.forms.SiteViewForm;
import edu.ualberta.med.biobank.forms.WsObjectInput;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ClinicNode;
import edu.ualberta.med.biobank.model.GroupNode;
import edu.ualberta.med.biobank.model.RootNode;
import edu.ualberta.med.biobank.model.SiteNode;
import edu.ualberta.med.biobank.model.SessionNode;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import edu.ualberta.med.biobank.model.Site;

public class SessionsView extends ViewPart {
	public static final String ID =
	      "edu.ualberta.med.biobank.session.SessionView";

	private TreeViewer treeViewer;
	
	private RootNode rootNode;
	
	private HashMap<String, SessionNode> sessions;
	
	private IDoubleClickListener doubleClickListener = new IDoubleClickListener() {
		@SuppressWarnings("unchecked")
		public void doubleClick(DoubleClickEvent event) {
			Object selection = event.getSelection();

			if (selection == null) return;

			Object element = ((StructuredSelection)selection).getFirstElement();

			treeViewer.expandToLevel(element, 1);

			if (element instanceof SiteNode) {
				openSiteNode((SiteNode) element);
			}
			else if (element instanceof GroupNode<?>) {
				GroupNode<?> groupNode = (GroupNode<?>) element;
				if (groupNode.getName().equals("Clinics")) {
					updateClinics((GroupNode<ClinicNode>) groupNode);
				}
			}
			else if (element instanceof ClinicNode) {
				openClinicNode((ClinicNode) element);
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

		@SuppressWarnings("unchecked")
		@Override
		public void treeExpanded(TreeExpansionEvent e) {
			Object o = e.getElement();
			if (o instanceof GroupNode) {
				GroupNode<ClinicNode> clinicGroupNode
					= (GroupNode<ClinicNode>) o;
				if (clinicGroupNode.getName().equals("Clinic")) {
					updateClinics(clinicGroupNode);
				}			
			}
		}
	};
	
	public SessionsView() {
		super();
		BioBankPlugin.getDefault().setSessionView(this);
		rootNode = new RootNode();
		sessions = new  HashMap<String, SessionNode>();
	}

	@Override
	public void createPartControl(Composite parent) {		
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		treeViewer.setLabelProvider(new SessionLabelProvider());
		treeViewer.setContentProvider(new SessionContentProvider());
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
		final SessionNode sessionNode = new SessionNode(appService, name);
		sessions.put(name, sessionNode);
		rootNode.addSessionNode(sessionNode);
		
		for (Object o : sites) {
			sessionNode.addSite((Site) o);
		}
		treeViewer.refresh();
		treeViewer.expandToLevel(2);
	}
	
	public SessionNode getSessionNode(String sessionName) {
		for (SessionNode node : rootNode.getSessions()) {
			if (node.getName().equals(sessionName)) return node;
		}
		Assert.isTrue(false, "Session with name " + sessionName
				+ " not found");
		return null;
	}
	
	public SessionNode getSessionNode(int count) {
		SessionNode[] nodes = rootNode.getSessions();
		Assert.isTrue(count < nodes.length, "Invalid session node count: " + count);
		return nodes[count];
	}
	
	public void updateSites(final SessionNode sessionNode) {
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
							for (Object obj : sites) {
								sessionNode.addSite((Site) obj);
							}
							treeViewer.refresh(sessionNode);
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
	
	public void updateClinics(final GroupNode<ClinicNode> groupNode) {
		String sessionName = groupNode.getParent().getParent().getName();
		Assert.isTrue(sessions.containsKey(sessionName), 
				"Session named " + sessionName + " not found");
		
		final SessionNode sessionNode = sessions.get(sessionName);
		
		// get the Site sites stored on this server
		Job job = new Job("Querying Clinics") {
			protected IStatus run(IProgressMonitor monitor) {
				
				monitor.beginTask("Querying Clinics ... ", 100);
				
				Clinic clinic = new Clinic();				
				try {
					WritableApplicationService appService = sessionNode.getAppService();
					final List<Object> sites = appService.search(Clinic.class, clinic);
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							for (Object obj : sites) {
								ClinicNode node = new ClinicNode(
										groupNode, (Clinic) obj);
								groupNode.addChild(node);
							}
							treeViewer.expandToLevel(groupNode, 1);
							treeViewer.refresh(groupNode);
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
		
		final SessionNode sessionNode = sessions.get(sessionName);
		
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
				catch (Exception exp) {
					exp.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	public void updateObject(final String sessionName, final Object o) throws Exception {
		Assert.isTrue(sessions.containsKey(sessionName), "Session named " + sessionName + " not found");
		
		final SessionNode sessionNode = sessions.get(sessionName);
		
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
									openSiteNode(sessionNode.getSite(id));
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
				catch (Exception exp) {
					exp.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	public void deleteSession(String name) throws Exception {
		rootNode.deleteSessionNode(name);
	}
	
	public int getSessionCount() {
		return rootNode.getChildCount();
	}
	
	public String[] getSessionNames() {
		return sessions.keySet().toArray(new String[sessions.size()]);
	}
	
	private void openSiteNode(SiteNode node) {
		WsObjectInput input = new WsObjectInput(node);
		
		try {
			getSite().getPage().openEditor(input, SiteViewForm.ID, true);
		} 
		catch (PartInitException e) {
			// handle error
			e.printStackTrace();				
		}
	}
	
	private void openClinicNode(ClinicNode node) {
		WsObjectInput input = new WsObjectInput(node);
		
		try {
			getSite().getPage().openEditor(input, ClinicViewForm.ID, true);
		} 
		catch (PartInitException e) {
			// handle error
			e.printStackTrace();				
		}
	}
}
