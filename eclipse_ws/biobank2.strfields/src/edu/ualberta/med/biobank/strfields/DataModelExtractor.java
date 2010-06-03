package edu.ualberta.med.biobank.strfields;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataModelExtractor {

    private class DataModelClass {
        @SuppressWarnings("unused")
        String name;

        Map<String, String> attrMap;

        public DataModelClass(String name) {
            this.name = name;
            attrMap = new HashMap<String, String>();
        }
    }

    private static DataModelExtractor instance = null;

    private Map<String, String> modelDataTypeMap;

    private Map<String, DataModelClass> dataModelClassMap;

    private DataModelExtractor() {
        modelDataTypeMap = new HashMap<String, String>();
        dataModelClassMap = new HashMap<String, DataModelClass>();
    }

    public static DataModelExtractor getInstance() {
        if (instance == null) {
            instance = new DataModelExtractor();
        }
        return instance;
    }

    public void getModel(String modelFileName) throws Exception {
        File file = new File(modelFileName);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        NodeList nodeLst = doc.getElementsByTagName("UML:Model");
        for (int i = 0, n = nodeLst.getLength(); i < n; i++) {
            Node node = nodeLst.item(i);
            if (!((Element) node).getAttribute("name").equals("Model"))
                continue;

            getModel(node);
        }

        nodeLst = doc.getElementsByTagName("UML:Package");
        for (int i = 0, n = nodeLst.getLength(); i < n; i++) {
            Node node = nodeLst.item(i);
            if (!((Element) node).getAttribute("name").equals("Data Model"))
                continue;

            getDataModel(node);
        }
    }

    public Set<String> getDmClassSet() throws Exception {
        if (dataModelClassMap.size() == 0) {
            throw new Exception("UML file not parsed yet");
        }
        return dataModelClassMap.keySet();
    }

    public Map<String, String> getDmClassAttrMap(String className)
        throws Exception {
        if (dataModelClassMap.size() == 0) {
            throw new Exception("UML file not parsed yet");
        }

        DataModelClass dmClass = dataModelClassMap.get(className);
        if (dmClass == null) {
            throw new Exception("invalid data model class name: " + className);
        }
        return dmClass.attrMap;
    }

    private void getModel(Node node) throws Exception {
        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals(
                "UML:Namespace.ownedElement"))
                continue;

            getModelOwnedElement(childNode);
        }
    }

    private void getModelOwnedElement(Node node) throws Exception {
        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals("UML:DataType"))
                continue;

            getModelDataType(childNode);
        }
    }

    private void getModelDataType(Node node) {
        modelDataTypeMap.put(((Element) node).getAttribute("xmi.id"),
            ((Element) node).getAttribute("name"));

    }

    private void getDataModel(Node node) throws Exception {
        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals(
                "UML:Namespace.ownedElement"))
                continue;

            getDmOwnedElement(childNode);
        }
    }

    private void getDmOwnedElement(Node node) throws Exception {
        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals("UML:Class"))
                continue;

            getDmClass(childNode);
        }
    }

    private void getDmClass(Node node) throws Exception {
        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals(
                "UML:Classifier.feature"))
                continue;

            getDmClassifierFeature(childNode);
        }
    }

    private void getDmClassifierFeature(Node node) throws Exception {
        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals("UML:Attribute"))
                continue;

            getDmClassAttribute(childNode);
        }
    }

    private void getDmClassAttribute(Node node) throws Exception {
        Node grandParent = node.getParentNode().getParentNode();
        if (grandParent == null) {
            throw new Exception(
                "UML:Attribute node does not have a grandparent");
        }

        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals(
                "UML:StructuralFeature.type"))
                continue;

            getDmClassAttrStructFeature(childNode);
        }
    }

    private void getDmClassAttrStructFeature(Node node) throws Exception {
        Node classParent = node.getParentNode().getParentNode().getParentNode();
        if (classParent == null) {
            throw new Exception(
                "UML:Attribute node does not have a class parent");
        }

        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals("UML:DataType"))
                continue;

            String className = ((Element) classParent).getAttribute("name");
            String attrName = ((Element) node.getParentNode())
                .getAttribute("name");
            String idref = ((Element) childNode).getAttribute("xmi.idref");
            String modelDataType = modelDataTypeMap.get(idref);
            if (modelDataType == null) {
                throw new Exception("could not find data type for " + className
                    + "." + attrName);
            }

            DataModelClass dmClass = dataModelClassMap.get(className);
            if (dmClass == null) {
                dmClass = new DataModelClass(className);
                dataModelClassMap.put(className, dmClass);
            }
            dmClass.attrMap.put(attrName, modelDataType);
        }
    }
}
