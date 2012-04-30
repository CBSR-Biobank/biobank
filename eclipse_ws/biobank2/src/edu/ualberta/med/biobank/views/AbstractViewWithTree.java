package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;

public abstract class AbstractViewWithTree<T> extends ViewPart {

    public abstract TreeViewer getTreeViewer();

    @Override
    public abstract void setFocus();

    @SuppressWarnings("unchecked")
    public T getSelectedNode() {
        if (getTreeViewer() != null) {
            IStructuredSelection treeSelection =
                (IStructuredSelection) getTreeViewer()
                    .getSelection();
            if (treeSelection != null && treeSelection.size() > 0) {
                return (T) treeSelection.getFirstElement();
            }
        }
        return null;
    }

    public void setSelectedNode(final T node) {
        if (getTreeViewer() != null) {
            getTreeViewer().setSelection(new StructuredSelection(node));
        }
    }

    // FIXME: converted this to asyncExec call due to issue #1039.
    // not sure if this is the right solution
    public void setSelectedNodeAsync(final T node) {
        if (getTreeViewer() == null)
            return;

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                getTreeViewer().setSelection(new StructuredSelection(node));
            }
        });
    }

    public abstract List<AbstractAdapterBase> searchNode(
        Class<?> searchedClass, Integer objectId);

}
