package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.List;

public class ReportTreeNode extends Object {
    private ReportTreeNode parent;
    private List<ReportTreeNode> children;
    private Class<?> objClass;
    private String name;

    public ReportTreeNode(String name, Class<?> objClass) {
        this.name = name;
        this.objClass = objClass;
        this.children = new ArrayList<ReportTreeNode>();
    }

    public String getLabel() {
        return name;
    }

    public Class<?> getObjClass() {
        return objClass;
    }

    public ReportTreeNode getParent() {
        return parent;
    }

    public List<ReportTreeNode> getChildren() {
        return children;
    }

    public boolean isRoot() {
        return (parent == null);
    }

    public boolean isLeaf() {
        return (children.size() == 0);
    }

    public void addChild(ReportTreeNode n) {
        children.add(n);
    }

    public void removeChild(ReportTreeNode n) {
        children.remove(n);
    }

    public void setParent(ReportTreeNode n) {
        parent = n;
    }

}
