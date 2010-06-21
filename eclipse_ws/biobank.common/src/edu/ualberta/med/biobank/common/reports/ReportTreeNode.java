package edu.ualberta.med.biobank.common.reports;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ReportTreeNode extends Object {
    private ReportTreeNode parent;
    private List<ReportTreeNode> children;
    private Object query;
    private String name;

    public ReportTreeNode(String name, Object query) {
        this.name = name;
        this.query = query;
        this.children = new ArrayList<ReportTreeNode>();
    }

    public String getLabel() {
        return name;
    }

    public String getToolTipText() {
        try {
            if (QueryObject.class.isAssignableFrom((Class<?>) query)) {

                Constructor<?> c = ((Class<?>) query).getConstructor(
                    String.class, Integer.class);
                QueryObject obj;
                obj = (QueryObject) c.newInstance("", 0);
                return obj.getDescription();

            }
        } catch (Exception e) {
        }
        return "";
    }

    public Object getQuery() {
        return query;
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
