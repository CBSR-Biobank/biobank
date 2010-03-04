package edu.ualberta.med.biobank.common.reports.advanced;

import java.util.ArrayList;
import java.util.List;

public class QueryTreeNode {
    private String label;
    private HQLField nodeInfo;
    private List<HQLField> fieldData;
    private QueryTreeNode parent;
    private List<QueryTreeNode> children;

    public QueryTreeNode(String label, HQLField nodeInfo) {
        this.label = label;
        this.nodeInfo = nodeInfo;
        this.children = new ArrayList<QueryTreeNode>();
    }

    public String getLabel() {
        return label;
    }

    public HQLField getNodeInfo() {
        return nodeInfo;
    }

    public QueryTreeNode getParent() {
        return parent;
    }

    public List<QueryTreeNode> getChildren() {
        return children;
    }

    public List<HQLField> getFieldData() {
        return fieldData;
    }

    public boolean isRoot() {
        return (parent == null);
    }

    public boolean isLeaf() {
        return (children.size() == 0);
    }

    public boolean isOperator() {
        return (fieldData == null);
    }

    public void addChild(QueryTreeNode n) {
        children.add(n);
    }

    public void removeChild(QueryTreeNode n) {
        children.remove(n);
    }

    public void setParent(QueryTreeNode n) {
        parent = n;
    }

    public String getTreePath() {
        if (isRoot())
            return label;
        else
            return parent.getLabel() + " > " + getLabel();
    }

}
