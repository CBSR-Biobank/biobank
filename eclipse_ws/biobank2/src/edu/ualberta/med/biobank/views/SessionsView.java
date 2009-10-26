package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;

/**
 * This view contains a tree view that represents the link to the server and the
 * ORM model objects in the database.
 */
public class SessionsView extends ViewPart implements IAdapterTreeView {

    public static final String ID = "edu.ualberta.med.biobank.views.SessionsView";

    private AdapterTreeWidget adaptersTree;

    public SessionsView() {
        SessionManager.getInstance().setSessionsView(this);
    }

    public TreeViewer getTreeViewer() {
        return adaptersTree.getTreeViewer();
    }

    @Override
    public void createPartControl(Composite parent) {
        adaptersTree = new AdapterTreeWidget(parent, this, true);

        getSite().setSelectionProvider(getTreeViewer());
        getTreeViewer().setInput(SessionManager.getInstance().getRootNode());
        SessionManager.getInstance().getRootNode().setTreeViewer(
            getTreeViewer());
        if (SessionManager.getInstance().getRootNode().hasChildren()) {
            getTreeViewer().expandToLevel(3);
        }
    }

    @Override
    public void setFocus() {
    }
};
