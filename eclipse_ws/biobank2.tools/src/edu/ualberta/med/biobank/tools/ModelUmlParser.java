package edu.ualberta.med.biobank.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelUmlParser {

    private class ModelClass {
        @SuppressWarnings("unused")
        private String name;

        private Map<String, String> attrMap;

        public ModelClass(String name) {
            this.name = name;
            attrMap = new HashMap<String, String>();
        }
    }

    private static ModelUmlParser instance = null;

    private Map<String, String> dataModelDataTypeMap;

    private Map<String, String> logicalModelDataTypeMap;

    private Map<String, ModelClass> dataModelClassMap;

    private Map<String, ModelClass> logicalModelClassMap;

    private ModelUmlParser() {
        dataModelDataTypeMap = new HashMap<String, String>();
        logicalModelDataTypeMap = new HashMap<String, String>();

        dataModelClassMap = new HashMap<String, ModelClass>();
        logicalModelClassMap = new HashMap<String, ModelClass>();
    }

    public static ModelUmlParser getInstance() {
        if (instance == null) {
            instance = new ModelUmlParser();
        }
        return instance;
    }

    public void geDataModel(String modelFileName) throws Exception {
        Document doc = getDocument(modelFileName);
        getDataModelDataTypes(doc);
        getDataModel(doc);
    }

    public void geLogicalModel(String modelFileName) throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory
            .newInstance();
        domFactory.setNamespaceAware(false);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(modelFileName);

        getLogicalModelDataTypes(doc);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath
            .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package/Namespace.ownedElement/Package/Namespace.ownedElement/Package/Namespace.ownedElement/Package/Namespace.ownedElement/Package/Namespace.ownedElement/Package/Namespace.ownedElement/Package/Namespace.ownedElement/Class");

        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0, n = nodes.getLength(); i < n; ++i) {
            Node node = nodes.item(i);

            String className = node.getAttributes().getNamedItem("name")
                .getNodeValue();
            ModelClass modelClass = new ModelClass(className);
            logicalModelClassMap.put(className, modelClass);

            System.out.println("logical model class: " + className);

            NodeList children = node.getChildNodes();
            for (int i1 = 0, n1 = children.getLength(); i1 < n1; ++i1) {
                if (!children.item(i1).getNodeName()
                    .equals("UML:Classifier.feature"))
                    continue;

                Node feature = children.item(i1);

                NodeList attributes = feature.getChildNodes();
                for (int i2 = 0, n2 = attributes.getLength(); i2 < n2; ++i2) {
                    if (!attributes.item(i2).getNodeName()
                        .equals("UML:Attribute"))
                        continue;
                    String classAttrName = attributes.item(i2).getAttributes()
                        .getNamedItem("name").getNodeValue();

                    System.out.println("logical model class attribute: "
                        + classAttrName);

                    String xmiIdRef = null;
                    NodeList attrChildren = attributes.item(i2).getChildNodes();
                    for (int i3 = 0, n3 = attrChildren.getLength(); i3 < n3; ++i3) {
                        if (!attrChildren.item(i3).getNodeName()
                            .equals("UML:StructuralFeature.type"))
                            continue;

                        xmiIdRef = attrChildren.item(i3).getChildNodes()
                            .item(1).getAttributes().getNamedItem("xmi.idref")
                            .getNodeValue();
                    }

                    if (xmiIdRef == null) {
                        throw new Exception("xmi.idref not found for class "
                            + className + " attribute " + classAttrName);
                    }

                    modelClass.attrMap.put(classAttrName,
                        logicalModelDataTypeMap.get(xmiIdRef));
                }
            }
        }
    }

    private Document getDocument(String modelFileName) throws Exception {
        File file = new File(modelFileName);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    public Set<String> getDmTableSet() throws Exception {
        if (dataModelClassMap.size() == 0) {
            throw new Exception("UML file not parsed yet");
        }
        return dataModelClassMap.keySet();
    }

    public Map<String, String> getDmTableAttrMap(String className)
        throws Exception {
        if (dataModelClassMap.size() == 0) {
            throw new Exception("UML file not parsed yet");
        }

        ModelClass dmTable = dataModelClassMap.get(className);
        if (dmTable == null) {
            throw new Exception("invalid data model class name: " + className);
        }
        return dmTable.attrMap;
    }

    private void getDataModel(Document doc) throws Exception {
        NodeList nodeLst = doc.getElementsByTagName("Class");
        for (int i = 0, n = nodeLst.getLength(); i < n; i++) {
            Node node = nodeLst.item(i);
            Node grandParent = node.getParentNode().getParentNode();
            if (grandParent == null) {
                throw new Exception("Class node does not have a grandparent");
            }
            if (!((Element) grandParent).getAttribute("name").equals(
                "Data Model"))
                continue;

            NodeList childNodeLst = node.getChildNodes();

            for (int i2 = 0, n2 = childNodeLst.getLength(); i2 < n2; ++i2) {
                Node childNode = (Node) childNodeLst.item(i2);
                if (!childNode.getNodeName().equals("Classifier.feature"))
                    continue;

                getDmClassifierFeature(childNode);
            }
        }
    }

    private void getDataModelDataTypes(Document doc) throws Exception {
        NodeList nodeLst = doc.getElementsByTagName("DataType");
        for (int i = 0, n = nodeLst.getLength(); i < n; i++) {
            Node node = nodeLst.item(i);
            Node grandParent = node.getParentNode().getParentNode();
            if (grandParent == null) {
                throw new Exception("DataType node does not have a grandparent");
            }
            if (!((Element) grandParent).getAttribute("name").equals("Model"))
                continue;

            dataModelDataTypeMap.put(((Element) node).getAttribute("xmi.id"),
                ((Element) node).getAttribute("name"));
        }
    }

    private void getLogicalModelDataTypes(Document doc) throws Exception {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath
            .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package/Namespace.ownedElement/Package/Namespace.ownedElement/Package/Namespace.ownedElement/Package/Namespace.ownedElement/Class");

        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0, n = nodes.getLength(); i < n; ++i) {
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();

            String lmTypeName = attrs.getNamedItem("name").getNodeValue();
            String lmXmiId = attrs.getNamedItem("xmi.id").getNodeValue();

            System.out.println("logical model type: " + lmTypeName + " id "
                + lmXmiId);

            logicalModelDataTypeMap.put(lmXmiId, lmTypeName);
        }
    }

    private void getDmClassifierFeature(Node node) throws Exception {
        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals("Attribute"))
                continue;

            getDmTableAttribute(childNode);
        }
    }

    private void getDmTableAttribute(Node node) throws Exception {
        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName()
                .equals("StructuralFeature.type"))
                continue;

            getDmClassAttrStructFeature(childNode);
        }
    }

    private void getDmClassAttrStructFeature(Node node) throws Exception {
        Node classParent = node.getParentNode().getParentNode().getParentNode();
        if (classParent == null) {
            throw new Exception("Attribute node does not have a class parent");
        }

        NodeList childNodeLst = node.getChildNodes();

        for (int i = 0, n = childNodeLst.getLength(); i < n; ++i) {
            Node childNode = (Node) childNodeLst.item(i);
            if (!childNodeLst.item(i).getNodeName().equals("DataType"))
                continue;

            String className = ((Element) classParent).getAttribute("name");
            String attrName = ((Element) node.getParentNode())
                .getAttribute("name");
            String idref = ((Element) childNode).getAttribute("xmi.idref");
            String modelDataType = dataModelDataTypeMap.get(idref);
            if (modelDataType == null) {
                throw new Exception("could not find data type for " + className
                    + "." + attrName);
            }

            ModelClass dmClass = dataModelClassMap.get(className);
            if (dmClass == null) {
                dmClass = new ModelClass(className);
                dataModelClassMap.put(className, dmClass);
            }
            dmClass.attrMap.put(attrName, modelDataType);
        }
    }
}
