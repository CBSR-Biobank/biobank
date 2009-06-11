package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.NodeContentProvider;
import edu.ualberta.med.biobank.treeview.NodeLabelProvider;

public class SessionsView extends ViewPart {

	public static final String ID = "edu.ualberta.med.biobank.views.SessionsView";

	private TreeViewer treeViewer;

	private TreeFilter treeFilter;

	public SessionsView() {
		SessionManager.getInstance().setSessionsView(this);
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public TreeFilter getFilter() {
		return treeFilter;
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
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (!selection.isEmpty()
						&& selection instanceof IStructuredSelection) {
					Node node = (Node) ((IStructuredSelection) selection)
						.getFirstElement();
					getViewSite().getActionBars().getStatusLineManager()
						.setMessage(node.getName());

				}
			}
		});
		treeViewer.setComparer(new IElementComparer() {
			@Override
			public boolean equals(Object a, Object b) {
				if (a instanceof Node && b instanceof Node) {
					return ((Node) a).isSameNode((Node) b);
				}
				return false;
			}

			@Override
			public int hashCode(Object element) {
				return element.hashCode();
			}

		});
		treeFilter = new TreeFilter();
		treeViewer.addFilter(treeFilter);

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
