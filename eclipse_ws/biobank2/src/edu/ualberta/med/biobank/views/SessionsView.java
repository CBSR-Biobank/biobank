package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import edu.ualberta.med.biobank.Activator;
import edu.ualberta.med.biobank.model.RootNode;
import edu.ualberta.med.biobank.model.BioBankNode;
import edu.ualberta.med.biobank.model.SessionNode;
import edu.ualberta.med.biobank.model.ISessionNodeListener;
import edu.ualberta.med.biobank.SessionContentProvider;
import edu.ualberta.med.biobank.SessionLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;

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
	
	public void addSession(ApplicationService appService, String name) {		
		SessionNode sessionNode = new SessionNode(appService, name);
		rootNode.addSessionNode(sessionNode);
		
		treeViewer.setInput(rootNode);
		sessionNode.addListener(new ISessionNodeListener() {
			public void sessionChanged(SessionNode sessionNode, BioBankNode bioBankNode) {
				treeViewer.refresh();
			}
		});
	}

}
