package edu.ualberta.med.biobank.widgets.multiselect;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.util.DeltaEvent;
import edu.ualberta.med.biobank.treeview.util.IDeltaListener;
import edu.ualberta.med.biobank.treeview.util.NullDeltaListener;

public class MultiSelectNode<T> {

    private T nodeObject;

    protected MultiSelectNode<T> parent;

    protected List<MultiSelectNode<T>> children;

    protected List<MultiSelectNode<T>> addedChildren;

    protected List<MultiSelectNode<T>> removedChildren;

    protected IDeltaListener listener = NullDeltaListener.getSoleInstance();

    public MultiSelectNode(MultiSelectNode<T> parent) {
        this.parent = parent;
        children = new ArrayList<MultiSelectNode<T>>();
    }

    public MultiSelectNode(MultiSelectNode<T> parent, T nodeObject) {
        this(parent);
        this.nodeObject = nodeObject;
        addedChildren = new ArrayList<MultiSelectNode<T>>();
        removedChildren = new ArrayList<MultiSelectNode<T>>();
    }

    public void setParent(MultiSelectNode<T> parent) {
        this.parent = parent;
    }

    public MultiSelectNode<T> getParent() {
        return parent;
    }

    public List<MultiSelectNode<T>> getChildren() {
        return children;
    }

    public List<MultiSelectNode<T>> getAddedChildren() {
        return addedChildren;
    }

    public List<MultiSelectNode<T>> getRemovedChildren() {
        return removedChildren;
    }

    public void addChild(MultiSelectNode<T> child) {
        child.setParent(this);
        children.add(child);
        addedChildren.add(child);
        removedChildren.remove(child);
        child.addListener(listener);
        fireAdd(child);
    }

    public void removeChild(MultiSelectNode<T> item) {
        if (children.size() == 0)
            return;

        MultiSelectNode<T> itemToRemove = null;

        for (MultiSelectNode<T> child : children) {
            if (child == item)
                itemToRemove = child;
        }

        if (itemToRemove != null) {
            children.remove(itemToRemove);
            removedChildren.add(itemToRemove);
            addedChildren.remove(itemToRemove);
            fireRemove(itemToRemove);
        }
    }

    public int getChildCount() {
        return children.size();
    }

    public boolean hasChild(ModelWrapper<?> child) {
        return children.contains(child);
    }

    public void addListener(IDeltaListener listener) {
        this.listener = listener;
    }

    public void removeListener(IDeltaListener listener) {
        if (this.listener.equals(listener)) {
            this.listener = NullDeltaListener.getSoleInstance();
        }
    }

    protected void fireAdd(Object added) {
        listener.add(new DeltaEvent(added));
    }

    protected void fireRemove(Object removed) {
        listener.remove(new DeltaEvent(removed));
    }

    /**
     * remove all current selections
     */
    public void clear() {
        for (MultiSelectNode<T> node : new ArrayList<MultiSelectNode<T>>(
            children)) {
            removeChild(node);
        }
        children.clear();
        addedChildren.clear();
        removedChildren.clear();
    }

    /**
     * Reset internal lists except current selections
     */
    public void reset() {
        addedChildren.clear();
        removedChildren.clear();
    }

    public T getNodeObject() {
        return nodeObject;
    }

}
