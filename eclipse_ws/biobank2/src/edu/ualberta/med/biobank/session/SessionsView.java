package edu.ualberta.med.biobank.session;

import java.util.HashMap;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
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
import edu.ualberta.med.biobank.forms.SiteForm;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.RootNode;
import edu.ualberta.med.biobank.model.SiteInput;
import edu.ualberta.med.biobank.model.SiteNode;
import edu.ualberta.med.biobank.model.SessionNode;
import edu.ualberta.med.biobank.model.ISessionNodeListener;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import edu.ualberta.med.biobank.model.Site;

public class SessionsView extends ViewPart implements IDoubleClickListener {
	public static final String ID =
	      "edu.ualberta.med.biobank.session.SessionView";

	private TreeViewer treeViewer;
	
	private RootNode rootNode;
	
	private HashMap<String, SessionNode> sessions;
	
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
        treeViewer.addDoubleClickListener(this);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
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
		job.setUser(true);
		job.schedule();
	}
	
	public void addSession(final WritableApplicationService appService, String name, 
			List<Object> sites) {
		final SessionNode sessionNode = new SessionNode(appService, name);
		sessions.put(name, sessionNode);
		rootNode.addSessionNode(sessionNode);
		
		treeViewer.setInput(rootNode);
		sessionNode.addListener(new ISessionNodeListener() {
			public void sessionChanged(SessionNode sessionNode, SiteNode siteNode) {
				treeViewer.refresh();
			}
		});
		
		for (Object obj : sites) {
			sessionNode.addSite((Site) obj);
		}
		treeViewer.refresh();
		treeViewer.expandToLevel(2);
	}
	
	public void updateSites(final String sessionName) throws Exception {
		if (!sessions.containsKey(sessionName)) {
			throw new Exception();
		}
		
		final SessionNode sessionNode = sessions.get(sessionName);
		
		// get the Site sites stored on this server
		Job job = new Job("logging in") {
			protected IStatus run(IProgressMonitor monitor) {
				
				monitor.beginTask("Querying Sites ... ", 100);
				
				Site site = new Site();				
				try {
					WritableApplicationService appService = sessionNode.getAppService();
					final List<Object> sites = appService.search(Site.class, site);
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							for (Object obj : sites) {
								Site site = (Site) obj;
								sessionNode.addSite(site);
							}
							treeViewer.expandToLevel(2);
						}
					});
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
						query = new InsertExampleQuery(site.getAddress());					
						result = appService.executeQuery(query);
						site.setAddress((Address) result.getObjectResult());
						query = new InsertExampleQuery(site);	
						appService.executeQuery(query);
					}
					
					updateSites(sessionName);
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

	@Override
	public void doubleClick(DoubleClickEvent event) {
		Object selection = event.getSelection();
		
		if (selection == null) return;
		
		Object element = ((StructuredSelection)selection).getFirstElement();

		if (element instanceof SiteNode) {
			SiteNode node = (SiteNode) element;
			SiteInput input = new SiteInput(node.getSite().getId(), node);
			
			try {
				getSite().getPage().openEditor(input, SiteForm.ID, true);
			} 
			catch (PartInitException e) {
				// handle error
				e.printStackTrace();				
			}
			
		}
	}
}
