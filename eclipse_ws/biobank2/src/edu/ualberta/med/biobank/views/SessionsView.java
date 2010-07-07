package edu.ualberta.med.biobank.views;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;

/**
 * This view contains a tree view that represents the link to the server and the
 * ORM model objects in the database.
 */
public class SessionsView extends AbstractViewWithAdapterTree {

    public static final String ID = "edu.ualberta.med.biobank.views.SessionsView";

    public SessionsView() {
        SessionManager.getInstance().setSessionsView(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        adaptersTree = new AdapterTreeWidget(parent, this, false);
        getSite().setSelectionProvider(getTreeViewer());
        rootNode = SessionManager.getInstance().getRootNode();
        getTreeViewer().setInput(rootNode);
        rootNode.setTreeViewer(getTreeViewer());
        if (rootNode.hasChildren()) {
            getTreeViewer().expandToLevel(3);
        }
    }

    @Override
    public void reload() {
        SessionManager.getInstance().rebuildSession();
    }
};
