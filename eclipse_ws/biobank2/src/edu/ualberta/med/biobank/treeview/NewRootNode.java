package edu.ualberta.med.biobank.treeview;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

public class NewRootNode extends AbstractNewAdapterBase {

    private TreeViewer treeViewer;

    public NewRootNode() {
        super(null, null, 1, "root", null, true); //$NON-NLS-1$
    }

    public void setTreeViewer(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
    }

    @Override
    public String getTooltipTextInternal() {
        return null;
    }

    public void expandChild(AbstractNewAdapterBase child) {
        if (treeViewer != null) {
            treeViewer.expandToLevel(child, 1);
        }
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return searchChildren(searchedClass, objectId);
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        return null;
    }

    @Override
    protected int getChildrenCount() throws Exception {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

}
