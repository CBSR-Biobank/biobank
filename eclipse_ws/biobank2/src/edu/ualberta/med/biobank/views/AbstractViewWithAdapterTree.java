package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.TreeViewer;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;

public abstract class AbstractViewWithAdapterTree extends
    AbstractViewWithTree<AdapterBase> {

    protected AdapterTreeWidget adaptersTree;

    protected RootNode rootNode;

    @Override
    public TreeViewer getTreeViewer() {
        if (adaptersTree == null) {
            return null;
        }
        return adaptersTree.getTreeViewer();
    }

    @Override
    public void setFocus() {
        adaptersTree.setFocus();
    }

    @Override
    public AdapterBase searchNode(ModelWrapper<?> wrapper) {
        return rootNode.accept(new NodeSearchVisitor(wrapper));
    }

    public abstract void reload();

    public void opened() {

    }
}
