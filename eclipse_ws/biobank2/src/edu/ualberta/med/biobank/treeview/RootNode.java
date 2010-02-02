package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
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
    public String getTitle() {
        return null;
    }

    public void expandChild(AdapterBase child) {
        if (treeViewer != null) {
            treeViewer.expandToLevel(child, 1);
        }
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

}
