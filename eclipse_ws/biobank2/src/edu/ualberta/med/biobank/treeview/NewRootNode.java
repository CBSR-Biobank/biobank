package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.model.IBiobankModel;

public class NewRootNode extends AbstractNewAdapterBase {

    private TreeViewer treeViewer;

    public NewRootNode() {
        super(null, 1, "root", true); //$NON-NLS-1$
    }

    public void setTreeViewer(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    public void expandChild(AbstractNewAdapterBase child) {
        if (treeViewer != null) {
            treeViewer.expandToLevel(child, 1);
        }
    }

    @Override
    public List<AbstractAdapterBase> search(Object searchedObject) {
        return searchChildren(searchedObject);
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
    protected Collection<? extends IBiobankModel> getChildrenObjects()
        throws Exception {
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

}
