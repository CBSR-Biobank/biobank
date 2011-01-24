package edu.ualberta.med.biobank.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelUmlParser {

    private static final Logger LOGGER = Logger.getLogger(ModelUmlParser.class
        .getName());

    private class ModelClass {
        private String name;

        private ModelClass extendsClass;

        private Map<String, String> attrMap;

        private Map<String, ClassAssociation> assocMap;

        public ModelClass(String name) {
            this.name = name;
            extendsClass = null;
            attrMap = new HashMap<String, String>();
            assocMap = new HashMap<String, ClassAssociation>();
        }
    }

    private class ClassAssociation {
        private ModelClass toClass;
        private String assocName;
        private String multiplicity;

        public ClassAssociation(ModelClass toClass, String assocName,
            String multiplicity) {
            this.toClass = toClass;
            this.assocName = assocName;
            this.multiplicity = multiplicity;
        }
    }

    @SuppressWarnings("unused")
    private class Generalization {
        ModelClass parentClass;
        ModelClass childClass;

        Generalization(ModelClass parentClass, ModelClass childClass) {
            this.parentClass = parentClass;
            this.childClass = childClass;
        }
    }

    private static ModelUmlParser instance = null;

    private Map<String, String> dataModelDataTypeMap;

    private Map<String, String> logicalModelDataTypeMap;

    private Map<String, ModelClass> dataModelClassMap;

    private Map<String, ModelClass> logicalModelClassMap;

    private Map<String, ModelClass> logicalModelXmiIdClassMap;

    @SuppressWarnings("unused")
    private Map<String, Generalization> lmGeneralizationXmiIdClassMap;

    private ModelUmlParser() {
        dataModelDataTypeMap = new HashMap<String, String>();
        logicalModelDataTypeMap = new HashMap<String, String>();

        dataModelClassMap = new HashMap<String, ModelClass>();
        logicalModelClassMap = new HashMap<String, ModelClass>();
        logicalModelXmiIdClassMap = new HashMap<String, ModelClass>();
    }

    public static ModelUmlParser getInstance() {
        if (instance == null) {
            instance = new ModelUmlParser();
        }
        return instance;
    }

    public void geLogicalModel(String modelFileName) throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory
            .newInstance();
        domFactory.setNamespaceAware(false);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(modelFileName);
        getLmClasses(doc);

        for (ModelClass modelClass : logicalModelClassMap.values()) {

            if (modelClass.extendsClass != null) {
                LOGGER.debug("class " + modelClass.name + " extends "
                    + modelClass.extendsClass + " {");
            } else {
                LOGGER.debug("class " + modelClass.name + "{");
            }
            for (String attr : modelClass.attrMap.keySet()) {
                LOGGER.debug("   " + modelClass.attrMap.get(attr) + " " + attr
                    + ";");
            }
            for (String assocName : modelClass.assocMap.keySet()) {
                ClassAssociation assoc = modelClass.assocMap.get(assocName);

                if (assoc.multiplicity.equals("0-*")
                    || assoc.multiplicity.equals("1-*")) {
                    LOGGER.debug("   Collection<" + assoc.toClass.name + "> "
                        + assocName + ";");
                } else {
                    LOGGER.debug("   " + assoc.toClass.name + " " + assocName
                        + ";");
                }
            }
            LOGGER.debug("};\n");
        }
    }

    private void addLogicalModelClass(String name, String xmiId) {
        ModelClass modelClass = new ModelClass(name);
        logicalModelClassMap.put(name, modelClass);
        logicalModelXmiIdClassMap.put(xmiId, modelClass);
        LOGGER.debug("LM class/" + name);
    }

    private void getLmClasses(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // path to get all logical model class names
        XPathExpression expr = xpath
            .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package[@name='Logical Model']"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement/Class");

        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0, n = nodes.getLength(); i < n; ++i) {
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();

            addLogicalModelClass(attrs.getNamedItem("name").getNodeValue(),
                attrs.getNamedItem("xmi.id").getNodeValue());
        }

        getLmClassAttributes(doc);
        getLmClassAssociations(doc);
    }

    @SuppressWarnings("unused")
    private void getLmClassGeneralizations(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr;
        NodeList nodes;

        for (ModelClass modelClass : logicalModelClassMap.values()) {
            // path to get all generalizations
            expr = xpath
                .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                    + "/Namespace.ownedElement/Package[@name='Logical Model']"
                    + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                    + "/Package/Namespace.ownedElement/Package"
                    + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                    + "/Package/Namespace.ownedElement/Generalization/@xmi.id");

            nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0, n = nodes.getLength(); i < n; ++i) {
                String xmiId = nodes.item(i).getNodeValue();

            }
        }

        for (ModelClass modelClass : logicalModelClassMap.values()) {
            // path to get all derived classes
            expr = xpath
                .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                    + "/Namespace.ownedElement/Package[@name='Logical Model']"
                    + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                    + "/Package/Namespace.ownedElement/Package"
                    + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                    + "/Package/Namespace.ownedElement/Class[@name='"
                    + modelClass.name
                    + "']/GeneralizableElement.generalization"
                    + "/Generalization/@xmi.idref");

            nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            if (nodes.getLength() == 1) {
                String xmiRefId = nodes.item(0).getNodeValue();
                modelClass.extendsClass = logicalModelXmiIdClassMap
                    .get(xmiRefId);
                LOGGER.debug("LM class/" + modelClass.name + " extends/"
                    + modelClass.extendsClass.name);
            } else if (nodes.getLength() > 1) {
                throw new Exception(
                    "generalization has more than one class node: className/"
                        + modelClass.name);
            }
        }
    }

    private void getLmClassAttributes(Document doc) throws Exception {
        getLmClassAttributeTypes(doc);

        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr;
        NodeList nodes;

        for (ModelClass modelClass : logicalModelClassMap.values()) {
            // path to get all logical model class attributes
            expr = xpath
                .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                    + "/Namespace.ownedElement/Package[@name='Logical Model']"
                    + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                    + "/Package/Namespace.ownedElement/Package"
                    + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                    + "/Package/Namespace.ownedElement/Class[@name='"
                    + modelClass.name + "']/Classifier.feature/Attribute/@name");

            nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0, n = nodes.getLength(); i < n; ++i) {
                String classAttrName = nodes.item(i).getNodeValue();
                modelClass.attrMap.put(classAttrName, "UNKNOWN");
                LOGGER.debug("LM class/" + modelClass.name + " attribute/"
                    + classAttrName);
            }
        }

        for (ModelClass modelClass : logicalModelClassMap.values()) {
            for (String classAttrName : modelClass.attrMap.keySet()) {
                // path to get all logical model class attribute types
                expr = xpath
                    .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement"
                        + "/Package/Namespace.ownedElement"
                        + "/Package[@name='Logical Model']"
                        + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                        + "/Package/Namespace.ownedElement/Package"
                        + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                        + "/Package/Namespace.ownedElement/Class[@name='"
                        + modelClass.name
                        + "']/Classifier.feature/Attribute[@name='"
                        + classAttrName
                        + "']/StructuralFeature.type/Class/@xmi.idref");

                nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0, n = nodes.getLength(); i < n; ++i) {
                    String xmiIdRef = nodes.item(i).getNodeValue();
                    if (modelClass.attrMap.get(classAttrName) == null) {
                        throw new Exception("class " + modelClass.name
                            + " does not have attribute " + classAttrName);
                    }

                    String classAttrType = logicalModelDataTypeMap
                        .get(xmiIdRef);

                    if (classAttrType == null) {
                        throw new Exception("class " + modelClass.name
                            + " does not have an attribute type for attribute"
                            + classAttrName);
                    }

                    modelClass.attrMap.put(classAttrName, classAttrType);
                    LOGGER.debug("LM class/" + modelClass.name + " attribute/"
                        + classAttrName + " type/" + classAttrType);
                }
            }
        }
    }

    private void getLmClassAttributeTypes(Document doc)
        throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr = xpath
            .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                + "/Package/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Class");

        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0, n = nodes.getLength(); i < n; ++i) {
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();

            String lmTypeName = attrs.getNamedItem("name").getNodeValue();
            String lmXmiId = attrs.getNamedItem("xmi.id").getNodeValue();

            LOGGER.debug("LM type/" + lmTypeName + " id/" + lmXmiId);

            logicalModelDataTypeMap.put(lmXmiId, lmTypeName);
        }
    }

    private void getLmClassAssociations(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // path to get all logical model class names
        XPathExpression expr = xpath
            .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package[@name='Logical Model']"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                + "/Association/@xmi.id");

        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0, n = nodes.getLength(); i < n; ++i) {

            String assocXmiId = nodes.item(i).getNodeValue();

            XPathExpression assocEndExpr = xpath
                .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                    + "/Namespace.ownedElement/Package[@name='Logical Model']"
                    + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                    + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                    + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                    + "/Association[@xmi.id='"
                    + assocXmiId
                    + "']/Association.connection/AssociationEnd");

            NodeList assocEndNodes = (NodeList) assocEndExpr.evaluate(doc,
                XPathConstants.NODESET);

            List<ClassAssociation> classAssocs = new ArrayList<ClassAssociation>();

            for (int i1 = 0, n1 = assocEndNodes.getLength(); i1 < n1; ++i1) {
                Node node = assocEndNodes.item(i1);
                NamedNodeMap attrs = node.getAttributes();

                String assocName = null;
                Node nameNode = attrs.getNamedItem("name");
                if (nameNode != null) {
                    assocName = nameNode.getNodeValue();
                }

                String endXmiId = attrs.getNamedItem("xmi.id").getNodeValue();
                XPathExpression classExpr = xpath
                    .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                        + "/Namespace.ownedElement/Package[@name='Logical Model']"
                        + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                        + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                        + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                        + "/Association[@xmi.id='"
                        + assocXmiId
                        + "']/Association.connection/AssociationEnd[@xmi.id='"
                        + endXmiId
                        + "']"
                        + "/AssociationEnd.participant/Class/@xmi.idref");

                NodeList assocClassNodes = (NodeList) classExpr.evaluate(doc,
                    XPathConstants.NODESET);
                if (assocClassNodes.getLength() != 1) {
                    throw new Exception(
                        "association end has more than one class node: assoc/"
                            + assocXmiId + " name/" + assocName);
                }

                String assocClassXmiIdRef = assocClassNodes.item(0)
                    .getNodeValue();

                ModelClass modelClass = logicalModelXmiIdClassMap
                    .get(assocClassXmiIdRef);
                if (modelClass == null) {
                    throw new Exception(
                        "class not found for association: assocname/"
                            + assocName + " xmi.idref/" + assocClassXmiIdRef);
                }

                classAssocs.add(new ClassAssociation(modelClass, assocName,
                    getMultiplicity(doc, assocXmiId, endXmiId)));
            }

            if (classAssocs.size() != 2) {
                throw new Exception("association with more than 2 classes");
            }

            if ((classAssocs.get(0).assocName != null)
                && (classAssocs.get(0).assocName.length() > 0)) {
                addClassAssoc(classAssocs.get(1).toClass.name,
                    classAssocs.get(0).assocName,
                    classAssocs.get(0).toClass.name,
                    classAssocs.get(0).multiplicity);
            }

            if ((classAssocs.get(1).assocName != null)
                && (classAssocs.get(1).assocName.length() > 0)) {
                addClassAssoc(classAssocs.get(0).toClass.name,
                    classAssocs.get(1).assocName,
                    classAssocs.get(1).toClass.name,
                    classAssocs.get(1).multiplicity);
            }
        }
    }

    private String getMultiplicity(Document doc, String assocXmiId,
        String endXmiId) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression multExpr = xpath
            .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package[@name='Logical Model']"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                + "/Association[@xmi.id='"
                + assocXmiId
                + "']/Association.connection/AssociationEnd[@xmi.id='"
                + endXmiId
                + "']"
                + "/AssociationEnd.multiplicity/Multiplicity/Multiplicity.range/MultiplicityRange");

        NodeList assocMultNodes = (NodeList) multExpr.evaluate(doc,
            XPathConstants.NODESET);
        if (assocMultNodes.getLength() != 1) {
            throw new Exception(
                "association end has more than one multiplicity node: assoc/"
                    + assocXmiId + " endId/" + endXmiId);
        }

        NamedNodeMap multAttrs = assocMultNodes.item(0).getAttributes();

        String lowerMult = multAttrs.getNamedItem("lower").getNodeValue();
        String upperMult = multAttrs.getNamedItem("upper").getNodeValue();

        String multiplicity = null;
        if (lowerMult.equals("1") && upperMult.equals("1")) {
            multiplicity = "1";
        } else if (lowerMult.equals("0") && upperMult.equals("1")) {
            multiplicity = "0-1";
        } else if (lowerMult.equals("1") && upperMult.equals("-1")) {
            multiplicity = "1-*";
        } else if (lowerMult.equals("0") && upperMult.equals("-1")) {
            multiplicity = "0-*";
        }
        if (multiplicity == null) {
            throw new Exception("Could not determine muliplicity");
        }
        return multiplicity;
    }

    private void addClassAssoc(String fromClassName, String assocName,
        String toClassName, String muliplicity) throws Exception {
        ModelClass fromModelClass = logicalModelClassMap.get(fromClassName);
        ModelClass toModelClass = logicalModelClassMap.get(toClassName);

        if (fromModelClass == null) {
            throw new Exception("class not found: " + fromClassName);
        }

        if (toModelClass == null) {
            throw new Exception("class not found: " + toClassName);
        }

        fromModelClass.assocMap.put(assocName, new ClassAssociation(
            toModelClass, assocName, muliplicity));

        LOGGER.debug("LM assoc: " + fromClassName + "." + assocName + " -> "
            + toClassName);
    }

    public void geDataModel(String modelFileName) throws Exception {
        Document doc = getDocument(modelFileName);
        getDataModelDataTypes(doc);
        getDataModel(doc);
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
