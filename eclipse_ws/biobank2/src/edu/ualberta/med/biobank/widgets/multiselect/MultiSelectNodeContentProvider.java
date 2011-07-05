package edu.ualberta.med.biobank.widgets.multiselect;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import edu.ualberta.med.biobank.treeview.util.DeltaEvent;
import edu.ualberta.med.biobank.treeview.util.IDeltaListener;

public class MultiSelectNodeContentProvider implements ITreeContentProvider,
    IDeltaListener {
    protected TreeViewer viewer;

    @Override
    public Object[] getChildren(Object parentElement) {
        Assert.isTrue(parentElement instanceof MultiSelectNode,
            "Invalid object");
        return ((MultiSelectNode) parentElement).getChildren().toArray();
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        Assert.isTrue(element instanceof MultiSelectNode, "Invalid object");
        return (((MultiSelectNode) element).getChildCount() > 0);
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public void dispose() {
        //
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (TreeViewer) viewer;
        if (oldInput != null) {
            removeListenerFrom((MultiSelectNode) oldInput);
        }
        if (newInput != null) {
            addListenerTo((MultiSelectNode) newInput);
        }
    }

    protected void addListenerTo(MultiSelectNode node) {
        node.addListener(this);
        for (MultiSelectNode child : node.getChildren()) {
            addListenerTo(child);
        }
    }

    protected void removeListenerFrom(MultiSelectNode node) {
        node.removeListener(this);
        for (MultiSelectNode child : node.getChildren()) {
            removeListenerFrom(child);
        }
    }

    @Override
    public void add(DeltaEvent event) {
        MultiSelectNode node = ((MultiSelectNode) event.receiver()).getParent();
        viewer.refresh(node, false);
    }

    @Override
    public void remove(DeltaEvent event) {
        add(event);
    }

}
