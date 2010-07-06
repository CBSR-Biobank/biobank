package edu.ualberta.med.biobank.client.reports;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ReportTreeNode extends Object {
    private ReportTreeNode parent;
    private List<ReportTreeNode> children;
    private Object report;
    private String name;

    public ReportTreeNode(String name, Object query) {
        this.name = name;
        this.report = query;
        this.children = new ArrayList<ReportTreeNode>();
    }

    public String getLabel() {
        return name;
    }

    public String getToolTipText() {
        try {
            if (AbstractReport.class.isAssignableFrom((Class<?>) report)) {
                Constructor<?> c = ((Class<?>) report).getConstructor(
                    String.class, Integer.class);
                AbstractReport obj = (AbstractReport) c.newInstance();
                return obj.getDescription();

            }
        } catch (Exception e) {
        }
        return "";
    }

    public Object getQuery() {
        return report;
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
