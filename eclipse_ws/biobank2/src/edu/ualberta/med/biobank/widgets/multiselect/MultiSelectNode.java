package edu.ualberta.med.biobank.widgets.multiselect;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.treeview.DeltaEvent;
import edu.ualberta.med.biobank.treeview.IDeltaListener;
import edu.ualberta.med.biobank.treeview.NullDeltaListener;

public class MultiSelectNode {
    private int id;

    private String name;

    protected MultiSelectNode parent;

    protected List<MultiSelectNode> children;

    protected IDeltaListener listener = NullDeltaListener.getSoleInstance();

    public MultiSelectNode(MultiSelectNode parent) {
        this.parent = parent;
        children = new ArrayList<MultiSelectNode>();
    }

    public MultiSelectNode(MultiSelectNode parent, int id, String name) {
        this(parent);
        setId(id);
        setName(name);
    }

    public void setParent(MultiSelectNode parent) {
        this.parent = parent;
    }

    public MultiSelectNode getParent() {
        return parent;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<MultiSelectNode> getChildren() {
        return children;
    }

    public void addChild(MultiSelectNode child) {
        child.setParent(this);
        children.add(child);
        child.addListener(listener);
        fireAdd(child);
    }

    public void insertAfter(MultiSelectNode existingNode,
        MultiSelectNode newNode) {
        int pos = children.indexOf(existingNode);
        Assert.isTrue(pos >= 0, "existing node not found: "
            + existingNode.getName());
        newNode.setParent(this);
        children.add(pos + 1, newNode);
        newNode.addListener(listener);
        fireAdd(newNode);
    }

    public void removeChild(MultiSelectNode item) {
        if (children.size() == 0)
            return;

        MultiSelectNode itemToRemove = null;

        for (MultiSelectNode child : children) {
            if (child == item)
                itemToRemove = child;
        }

        if (itemToRemove != null) {
            children.remove(itemToRemove);
            fireRemove(itemToRemove);
        }
    }

    public int getChildCount() {
        return children.size();
    }

    public boolean hasChild(String name) {
        for (MultiSelectNode child : children) {
            if (child.getName().equals(name))
                return true;
        }
        return false;
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

    @Override
    public String toString() {
        return name;
    }

    public void clear() {
        for (MultiSelectNode node : new ArrayList<MultiSelectNode>(children)) {
            removeChild(node);
        }
    }
}
