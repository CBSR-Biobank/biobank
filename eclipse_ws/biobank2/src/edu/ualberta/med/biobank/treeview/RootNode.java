package edu.ualberta.med.biobank.treeview;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RootNode extends AdapterBase {

    private TreeViewer treeViewer;

    public RootNode() {
        super(null, 1, "root");
    }

    public void setTreeViewer(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    @Override
    public WritableApplicationService getAppService() {
        return SessionManager.getInstance().getSession().getAppService();
    }

    @Override
    public void performDoubleClick() {
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
    }

    @Override
    public void loadChildren(boolean updateNode) {
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    public void expandChild(AdapterBase child) {
        if (treeViewer != null) {
            treeViewer.expandToLevel(child, 1);
        }
    }

    @Override
    protected Object getModelObject() {
        return null;
    }
}
