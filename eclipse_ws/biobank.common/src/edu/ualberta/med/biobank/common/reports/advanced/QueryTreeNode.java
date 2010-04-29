package edu.ualberta.med.biobank.common.reports.advanced;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

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
        if (nodeInfo.getFname().contains("Collection")) {
            String newName = nodeInfo.getFname().replace("Collection", "");
            if (newName.endsWith("y")) {
                newName = newName.replace("y", "ies");
            } else
                newName += "s";
            return newName;
        }
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
        QueryTreeNode node = new QueryTreeNode(new HQLField(this.getNodeInfo()));
        List<HQLField> fields = this.getFieldData();
        for (HQLField field : fields)
            node.addField(new HQLField(field));
        node.setParent(this.getParent());
        List<QueryTreeNode> children = this.getChildren();
        for (QueryTreeNode child : children) {
            QueryTreeNode childClone = child.clone();
            node.addChild(childClone);
            childClone.setParent(node);
        }
        return node;
    }

    public void insertField(int index, HQLField addedField) {
        fieldData.add(index + 1, addedField);
    }

    public void saveTree(String path, String name) throws IOException {
        XStream xStream = new XStream();
        xStream.alias("QueryTreeNode", QueryTreeNode.class);
        File file = new File(path);
        file.mkdirs();
        FileWriter fw = new FileWriter(file + "/" + name + ".xml");

        fw.write(xStream.toXML(this));
        fw.close();
    }

    public static QueryTreeNode getTreeFromFile(File file) throws IOException {

        XStream xStream = new XStream();
        xStream.alias("QueryTreeNode", QueryTreeNode.class);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder xml = new StringBuilder();
        while (reader.ready()) {
            xml.append(reader.readLine());
        }
        reader.close();
        return (QueryTreeNode) xStream.fromXML(xml.toString());

    }
}
