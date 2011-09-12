package edu.ualberta.med.biobank.widgets.multiselect;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import edu.ualberta.med.biobank.treeview.util.DeltaEvent;
import edu.ualberta.med.biobank.treeview.util.IDeltaListener;

public class MultiSelectNodeContentProvider<T> implements ITreeContentProvider,
    IDeltaListener {
    protected TreeViewer viewer;

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getChildren(Object parentElement) {
        Assert.isTrue(parentElement instanceof MultiSelectNode,
            "Invalid object"); //$NON-NLS-1$
        return ((MultiSelectNode<T>) parentElement).getChildren().toArray();
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasChildren(Object element) {
        Assert.isTrue(element instanceof MultiSelectNode, "Invalid object"); //$NON-NLS-1$
        return (((MultiSelectNode<T>) element).getChildCount() > 0);
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public void dispose() {
        //
    }

    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (TreeViewer) viewer;
        if (oldInput != null) {
            removeListenerFrom((MultiSelectNode<T>) oldInput);
        }
        if (newInput != null) {
            addListenerTo((MultiSelectNode<T>) newInput);
        }
    }

    protected void addListenerTo(MultiSelectNode<T> node) {
        node.addListener(this);
        for (MultiSelectNode<T> child : node.getChildren()) {
            addListenerTo(child);
        }
    }

    protected void removeListenerFrom(MultiSelectNode<T> node) {
        node.removeListener(this);
        for (MultiSelectNode<T> child : node.getChildren()) {
            removeListenerFrom(child);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void add(DeltaEvent event) {
        MultiSelectNode<T> node = ((MultiSelectNode<T>) event.receiver())
            .getParent();
        viewer.refresh(node, false);
    }

    @Override
    public void remove(DeltaEvent event) {
        add(event);
    }

}
