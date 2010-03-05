package edu.ualberta.med.biobank.common.reports.advanced;

import java.util.ArrayList;
import java.util.List;


public class QueryTreeNode extends Object {
    private HQLField nodeInfo;
    private List<HQLField> fieldData;
    private QueryTreeNode parent;
    private List<QueryTreeNode> children;

    public QueryTreeNode(HQLField nodeInfo) {
        this.nodeInfo = nodeInfo;
        this.fieldData = new ArrayList<HQLField>();
        this.children = new ArrayList<QueryTreeNode>();
    }

    public String getLabel() {
        return nodeInfo.getFname();
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
        return (nodeInfo == null);
    }

    public void addChild(QueryTreeNode n) {
        children.add(n);
    }

    public void removeChild(QueryTreeNode n) {
        children.remove(n);
    }

    public void addField(HQLField f) {
        fieldData.add(f);
    }

    public void removeField(HQLField f) {
        fieldData.remove(f);
    }

    public void setParent(QueryTreeNode n) {
        parent = n;
    }

    public String getTreePath() {
        if (isRoot())
            return getLabel();
        else
            return parent.getTreePath() + " > " + getLabel();
    }

    @Override
    public QueryTreeNode clone() {
        QueryTreeNode node = new QueryTreeNode(this.getNodeInfo());
        List<HQLField> fields = this.getFieldData();
        for (HQLField field : fields)
            node.addField(field);
        node.setParent(this.getParent());
        List<QueryTreeNode> children = this.getChildren();
        for (QueryTreeNode child : children)
            node.addChild(child.clone());
        return node;
    }

}
