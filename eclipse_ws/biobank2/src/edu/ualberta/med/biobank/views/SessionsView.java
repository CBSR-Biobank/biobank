package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.treeview.NodeContentProvider;
import edu.ualberta.med.biobank.treeview.NodeLabelProvider;

public class SessionsView extends ViewPart {

	public static final String ID = "edu.ualberta.med.biobank.views.SessionsView";

	private TreeViewer treeViewer;

	public SessionsView() {
		SessionManager.getInstance().setSessionsView(this);
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		treeViewer.setLabelProvider(new NodeLabelProvider());
		treeViewer.setContentProvider(new NodeContentProvider());
		treeViewer.addDoubleClickListener(SessionManager.getInstance()
			.getDoubleClickListener());
		treeViewer.addTreeListener(SessionManager.getInstance()
			.getTreeViewerListener());
		treeViewer.setUseHashlookup(true);
		treeViewer.setInput(SessionManager.getInstance().getRootNode());

		Menu menu = new Menu(PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getShell(), SWT.NONE);
		menu.addListener(SWT.Show, SessionManager.getInstance()
			.getTreeViewerMenuListener());

		treeViewer.getTree().setMenu(menu);

	}

	@Override
	public void setFocus() {
	}
};
