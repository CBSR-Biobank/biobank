package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public abstract class AbstractViewWithTree<T> extends ViewPart {

    public abstract TreeViewer getTreeViewer();

    @Override
    public abstract void setFocus();

    @SuppressWarnings("unchecked")
    public T getSelectedNode() {
        if (getTreeViewer() != null) {
            IStructuredSelection treeSelection = (IStructuredSelection) getTreeViewer()
                .getSelection();
            if (treeSelection != null && treeSelection.size() > 0) {
                return (T) treeSelection.getFirstElement();
            }
        }
        return null;
    }

    public void setSelectedNode(T node) {
        if (getTreeViewer() != null) {
            getTreeViewer().setSelection(new StructuredSelection(node));
        }
    }

    public abstract AdapterBase searchNode(ModelWrapper<?> wrapper);

}
