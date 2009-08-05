package edu.ualberta.med.biobank.views;

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
import edu.ualberta.med.biobank.treeview.AdaptorBase;
import edu.ualberta.med.biobank.treeview.NodeContentProvider;
import edu.ualberta.med.biobank.treeview.NodeLabelProvider;

/**
 * This view contains a tree view that represents the link to the server and the
 * ORM model objects in the database.
 */
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
        // FilteredTree filteredTree = new FilteredTree(parent, SWT.BORDER
        // | SWT.MULTI | SWT.V_SCROLL, new TreeFilter(), true);
        // filteredTree.setBackground(parent.getDisplay().getSystemColor(
        // SWT.COLOR_LIST_BACKGROUND));
        // filteredTree
        // .setCursor(new Cursor(parent.getDisplay(), SWT.CURSOR_HAND));

        treeViewer = new TreeViewer(parent);
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
                    AdaptorBase node = (AdaptorBase) ((IStructuredSelection) selection)
                        .getFirstElement();
                    getViewSite().getActionBars().getStatusLineManager()
                        .setMessage(node.getName());

                }
            }
        });

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
