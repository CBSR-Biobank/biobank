package edu.ualberta.med.biobank.views;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import edu.ualberta.med.biobank.Activator;
import edu.ualberta.med.biobank.SessionCredentials;
import edu.ualberta.med.biobank.model.RootNode;
import edu.ualberta.med.biobank.model.BioBankNode;
import edu.ualberta.med.biobank.model.SessionNode;
import edu.ualberta.med.biobank.model.ISessionNodeListener;
import edu.ualberta.med.biobank.SessionContentProvider;
import edu.ualberta.med.biobank.SessionLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;
import edu.ualberta.med.biobank.model.BioBank;

public class SessionsView extends ViewPart {
	public static final String ID =
	      "edu.ualberta.med.biobank.views.sessions";

	private TreeViewer treeViewer;
	
	private RootNode rootNode;
	
	public SessionsView() {
		super();
		Activator.getDefault().setSessionView(this);
		rootNode = new RootNode();
	}

	@Override
	public void createPartControl(Composite parent) {
		
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		treeViewer.setLabelProvider(new SessionLabelProvider());
		treeViewer.setContentProvider(new SessionContentProvider());
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
	
	public void loginFailed(SessionCredentials sc) {
		// pop up a dialog box here
		
	}
	
	public void addSession(final ApplicationService appService, final String name) {	
		final SessionNode sessionNode = new SessionNode(appService, name);
		rootNode.addSessionNode(sessionNode);
		
		treeViewer.setInput(rootNode);
		sessionNode.addListener(new ISessionNodeListener() {
			public void sessionChanged(SessionNode sessionNode, BioBankNode bioBankNode) {
				treeViewer.refresh();
			}
		});

		// get the BioBank sites stored on this server
		Job job = new Job("logging in") {
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Logging in ... ", 100);
				
				BioBank bioBank = new BioBank();				
				try {
					final List<Object> bioBanks = appService.search(BioBank.class, bioBank);
					
					Display.getDefault().asyncExec(new Runnable() {
				          public void run() {
				        	  Activator.getDefault().getSessionView().addBioBanks(sessionNode, bioBanks);
				          }
					});
				}
				catch (Exception e) {
					System.out.println(">>>" + e.getMessage());
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	public void addBioBanks(SessionNode sessionNode, List<Object> bioBanks) {
		for (Object obj : bioBanks) {
			sessionNode.addBioBank((BioBank) obj);
		}
		
	}

}
