package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.List;

public class AbstractReportTreeNode {
    private AbstractReportTreeNode parent;
    private List<AbstractReportTreeNode> children;
    private String name;

    public AbstractReportTreeNode(String name) {
        this.name = name;
        this.children = new ArrayList<AbstractReportTreeNode>();
    }

    public String getLabel() {
        return name;
    }

    public AbstractReportTreeNode getParent() {
        return parent;
    }

    public List<AbstractReportTreeNode> getChildren() {
        return children;
    }

    public boolean isRoot() {
        return (parent == null);
    }

    public boolean isLeaf() {
        return (children.size() == 0);
    }

    public void addChild(AbstractReportTreeNode n) {
        children.add(n);
    }

    public void removeChild(AbstractReportTreeNode n) {
        children.remove(n);
    }

    public void setParent(AbstractReportTreeNode n) {
        parent = n;
    }

    public String getToolTipText() {
        return null;
    }

    public void removeAll() {
        children.clear();
    }
}
