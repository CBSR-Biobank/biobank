package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;

public abstract class AbstractViewWithTree extends ViewPart implements
    IAdapterTreeView {

    protected AdapterTreeWidget adaptersTree;

    protected RootNode rootNode;

    public TreeViewer getTreeViewer() {
        return adaptersTree.getTreeViewer();
    }

    @Override
    public void setFocus() {
        adaptersTree.setFocus();
    }

    public AdapterBase getSelectedNode() {
        IStructuredSelection treeSelection = (IStructuredSelection) getTreeViewer()
            .getSelection();
        if (treeSelection != null && treeSelection.size() > 0) {
            return (AdapterBase) treeSelection.getFirstElement();
        }
        return null;
    }

    public abstract void reload();

    public AdapterBase searchNode(ModelWrapper<?> wrapper) {
        return rootNode.accept(new NodeSearchVisitor(wrapper));
    }

    public void setSelectedNode(AdapterBase node) {
        if (adaptersTree != null) {
            adaptersTree.getTreeViewer().setSelection(
                new StructuredSelection(node));
        }
    }

    public void opened() {

    }
}
