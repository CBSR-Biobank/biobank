package edu.ualberta.med.biobank.tools.modelumlparser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelUmlParser {

    private static final Logger LOGGER = Logger.getLogger(ModelUmlParser.class
        .getName());

    private static ModelUmlParser instance = null;

    private Map<String, String> dataModelDataTypeMap;

    private Map<String, ModelClass> dataModelClassMap;

    private Map<String, String> logicalModelDataTypeMap;

    private Map<String, ModelClass> logicalModelClassMap;

    private Map<String, ModelClass> logicalModelXmiIdClassMap;

    private Map<String, Generalization> generalizationMap;

    @SuppressWarnings("unused")
    private Map<String, Generalization> lmGeneralizationXmiIdClassMap;

    private ModelUmlParser() {
        dataModelDataTypeMap = new HashMap<String, String>();
        logicalModelDataTypeMap = new HashMap<String, String>();

        dataModelClassMap = new HashMap<String, ModelClass>();
        logicalModelClassMap = new HashMap<String, ModelClass>();
        logicalModelXmiIdClassMap = new HashMap<String, ModelClass>();
        generalizationMap = new HashMap<String, Generalization>();
    }

    public static ModelUmlParser getInstance() {
        if (instance == null) {
            instance = new ModelUmlParser();
        }
        return instance;
    }

    public Map<String, ModelClass> geLogicalModel(String modelFileName)
        throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory
            .newInstance();
        domFactory.setNamespaceAware(false);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(modelFileName);
        getLmClasses(doc);

        // displayModelInLogger();

        return Collections.unmodifiableMap(logicalModelClassMap);
    }

    @SuppressWarnings("unused")
    private void displayModelInLogger() {
        for (ModelClass modelClass : logicalModelClassMap.values()) {

            if (modelClass.getExtendsClass() != null) {
                LOGGER.debug("class " + modelClass.getName() + " extends "
                    + modelClass.getExtendsClass().getName() + " {");
            } else {
                LOGGER.debug("class " + modelClass.getName() + "{");
            }
            for (String attr : modelClass.getAttrMap().keySet()) {
                LOGGER.debug("   " + modelClass.getAttrMap().get(attr) + " "
                    + attr + ";");
            }
            for (String assocName : modelClass.getAssocMap().keySet()) {
                ClassAssociation assoc = modelClass.getAssocMap()
                    .get(assocName);

                if (assoc.getMultiplicity().equals("0-*")
                    || assoc.getMultiplicity().equals("1-*")) {
                    LOGGER
                        .debug("   Collection<" + assoc.getToClass().getName()
                            + "> " + assocName + ";");
                } else {
                    LOGGER.debug("   " + assoc.getToClass().getName() + " "
                        + assocName + ";");
                }
            }
            LOGGER.debug("};\n");
        }
    }

    private ModelClass addLogicalModelClass(String name, String xmiId) {
        ModelClass modelClass = new ModelClass(name);
        logicalModelClassMap.put(name, modelClass);
        logicalModelXmiIdClassMap.put(xmiId, modelClass);
        LOGGER.debug("LM class/" + name);
        return modelClass;
    }

    private void getLmClasses(Document doc) throws Exception {
        getLmClassAttributeTypes(doc);

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

            ModelClass mc = addLogicalModelClass(attrs.getNamedItem("name")
                .getNodeValue(), attrs.getNamedItem("xmi.id").getNodeValue());

            getLmClassAttributes(node, mc);
        }

        getLmClassAssociations(doc);
        getLmClassGeneralizations(doc);
    }

    private void getLmClassGeneralizations(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr;
        NodeList nodes;

        // path to get all generalizations
        expr = xpath
            .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package[@name='Logical Model']"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                + "/Package/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package/Namespace.ownedElement"
                + "/Package/Namespace.ownedElement/Generalization");

        nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0, n = nodes.getLength(); i < n; ++i) {
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();

            String xmiId = attrs.getNamedItem("xmi.id").getNodeValue();

            XPathExpression childExpr = xpath
                .compile("Generalization.child/Class/@xmi.idref");

            NodeList childNodes = (NodeList) childExpr.evaluate(node,
                XPathConstants.NODESET);
            if (childNodes.getLength() != 1) {
                throw new Exception(
                    "Generalization does not have only one child");
            }
            String childXmiIdRef = childNodes.item(0).getNodeValue();

            XPathExpression parentExpr = xpath
                .compile("Generalization.parent/Class/@xmi.idref");

            NodeList parentNodes = (NodeList) parentExpr.evaluate(node,
                XPathConstants.NODESET);
            if (parentNodes.getLength() != 1) {
                throw new Exception("Generalization has more than one parent");
            }
            String parentXmiIdRef = parentNodes.item(0).getNodeValue();

            ModelClass parentClass = logicalModelXmiIdClassMap
                .get(parentXmiIdRef);
            ModelClass childClass = logicalModelXmiIdClassMap
                .get(childXmiIdRef);

            if ((parentClass == null) || (childClass == null)) {
                throw new Exception("generalization classes not found: parent/"
                    + parentXmiIdRef + " child/" + childXmiIdRef);
            }

            Generalization g = new Generalization(parentClass, childClass);
            generalizationMap.put(xmiId, g);

            LOGGER.debug("Generalization: " + g.getParentClass().getName()
                + " <- " + g.getChildClass().getName());
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
                    + modelClass.getName()
                    + "']/GeneralizableElement.generalization"
                    + "/Generalization/@xmi.idref");

            nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            if (nodes.getLength() == 1) {
                String xmiIdRef = nodes.item(0).getNodeValue();

                Generalization g = generalizationMap.get(xmiIdRef);

                if (g == null) {
                    throw new Exception("generalization not found" + xmiIdRef);
                }

                if (g.getChildClass() != modelClass) {
                    throw new Exception(
                        "generalization child does not match class");
                }

                modelClass.setExtendsClass(g.getParentClass());

                LOGGER.debug("LM class/" + modelClass.getName() + " parent/"
                    + g.getParentClass().getName() + " child/"
                    + g.getChildClass().getName());
            } else if (nodes.getLength() > 1) {
                throw new Exception(
                    "generalization has more than one class node: className/"
                        + modelClass.getName());
            }
        }
    }

    private void getLmClassAttributes(Node node, ModelClass modelClass)
        throws Exception {
        if (logicalModelDataTypeMap.size() == 0) {
            throw new Exception("logical model data types not parsed yet");
        }

        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("Classifier.feature/Attribute");
        NodeList nodes = (NodeList) expr.evaluate(node, XPathConstants.NODESET);

        for (int i = 0, n = nodes.getLength(); i < n; ++i) {
            Node attrNode = nodes.item(i);
            NamedNodeMap attrs = attrNode.getAttributes();
            String classAttrName = attrs.getNamedItem("name").getNodeValue();

            // path to get model class attribute type
            expr = xpath.compile("StructuralFeature.type/Class/@xmi.idref");
            NodeList typeNodes = (NodeList) expr.evaluate(attrNode,
                XPathConstants.NODESET);

            if (typeNodes.getLength() != 1) {
                throw new Exception(
                    "Attribute does not have only one type child");
            }

            String xmiIdRef = typeNodes.item(0).getNodeValue();
            String classAttrType = logicalModelDataTypeMap.get(xmiIdRef);

            if (classAttrType == null) {
                throw new Exception(
                    "logical model does not have attribute type for xmi id"
                        + xmiIdRef);
            }

            modelClass.getAttrMap().put(classAttrName, classAttrType);
            LOGGER.debug("LM class/" + modelClass.getName() + " attribute/"
                + classAttrName + " type/" + classAttrType);
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
                + "/Association");

        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0, n = nodes.getLength(); i < n; ++i) {
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();

            String assocXmiId = attrs.getNamedItem("xmi.id").getNodeValue();

            XPathExpression assocEndExpr = xpath
                .compile("Association.connection/AssociationEnd");

            NodeList assocEndNodes = (NodeList) assocEndExpr.evaluate(node,
                XPathConstants.NODESET);

            List<ClassAssociation> classAssocs = new ArrayList<ClassAssociation>();

            for (int i1 = 0, n1 = assocEndNodes.getLength(); i1 < n1; ++i1) {
                Node assocEndNode = assocEndNodes.item(i1);
                NamedNodeMap assocendNodeAttrs = assocEndNode.getAttributes();

                String assocName = null;
                Node nameNode = assocendNodeAttrs.getNamedItem("name");
                if (nameNode != null) {
                    assocName = nameNode.getNodeValue();
                }

                String endXmiId = attrs.getNamedItem("xmi.id").getNodeValue();
                XPathExpression classExpr = xpath
                    .compile("AssociationEnd.participant/Class/@xmi.idref");

                NodeList assocClassNodes = (NodeList) classExpr.evaluate(
                    assocEndNode, XPathConstants.NODESET);
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
                    getLmMultiplicity(assocEndNode, assocXmiId, endXmiId)));
            }

            if (classAssocs.size() != 2) {
                throw new Exception("association with more than 2 classes");
            }

            if ((classAssocs.get(0).getAssocName() != null)
                && (classAssocs.get(0).getAssocName().length() > 0)) {
                addClassAssoc(classAssocs.get(1).getToClass().getName(),
                    classAssocs.get(0).getAssocName(), classAssocs.get(0)
                        .getToClass().getName(), classAssocs.get(0)
                        .getMultiplicity());
            }

            if ((classAssocs.get(1).getAssocName() != null)
                && (classAssocs.get(1).getAssocName().length() > 0)) {
                addClassAssoc(classAssocs.get(0).getToClass().getName(),
                    classAssocs.get(1).getAssocName(), classAssocs.get(1)
                        .getToClass().getName(), classAssocs.get(1)
                        .getMultiplicity());
            }
        }
    }

    private String getLmMultiplicity(Node node, String assocXmiId,
        String endXmiId) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression multExpr = xpath
            .compile("AssociationEnd.multiplicity/Multiplicity"
                + "/Multiplicity.range/MultiplicityRange");

        NodeList assocMultNodes = (NodeList) multExpr.evaluate(node,
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

        fromModelClass.getAssocMap().put(assocName,
            new ClassAssociation(toModelClass, assocName, muliplicity));

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
        return Collections.unmodifiableMap(dmTable.getAttrMap());
    }

    private void getDataModelDataTypes(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // path to get all logical model class names
        XPathExpression expr = xpath
            .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/DataType");

        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        int n = nodes.getLength();
        if (n == 0) {
            throw new Exception("DataType nodes not found in UML");
        }
        for (int i = 0; i < n; i++) {
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();
            dataModelDataTypeMap.put(attrs.getNamedItem("xmi.id")
                .getNodeValue(), attrs.getNamedItem("name").getNodeValue());
        }
    }

    private void getDataModel(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // path to get all logical model class names
        XPathExpression expr = xpath
            .compile("uml/XMI/XMI.content/Model/Namespace.ownedElement/Package"
                + "/Namespace.ownedElement/Package[@name='Data Model']"
                + "/Namespace.ownedElement/Class");

        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            NamedNodeMap attrs = nodes.item(i).getAttributes();

            ModelClass mc = new ModelClass(attrs.getNamedItem("name")
                .getNodeValue());
            dataModelClassMap.put(mc.getName(), mc);
            getDmTableAttributes(nodes.item(i), mc);
        }
    }

    private void getDmTableAttributes(Node node, ModelClass mc)
        throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // path to get all logical model class names
        XPathExpression expr = xpath.compile("Classifier.feature/Attribute");

        NodeList attrNodes = (NodeList) expr.evaluate(node,
            XPathConstants.NODESET);

        for (int i = 0, n = attrNodes.getLength(); i < n; ++i) {
            Node attrNode = attrNodes.item(i);
            NamedNodeMap attrs = node.getAttributes();

            String attrName = attrs.getNamedItem("name").getNodeValue();

            // path to get all logical model class names
            XPathExpression dataTypeExpr = xpath
                .compile("StructuralFeature.type/DataType/@xmi.idref");

            NodeList typeNodes = (NodeList) dataTypeExpr.evaluate(attrNode,
                XPathConstants.NODESET);

            int typeNodesCount = typeNodes.getLength();
            if (typeNodesCount != 1) {
                throw new Exception(
                    "attribute node does not have a type or more than one type");
            }

            String xmiIdRef = typeNodes.item(0).getNodeValue();
            String attrType = dataModelDataTypeMap.get(xmiIdRef);

            if (attrType == null) {
                throw new Exception("xmi id ref not found: " + xmiIdRef);
            }

            mc.getAttrMap().put(attrName, attrType);
        }
    }
}
