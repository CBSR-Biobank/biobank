package edu.ualberta.med.biobank.strfields;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataModelExtractor {

    private static DataModelExtractor instance = null;

    private DataModelExtractor() {

    }

    public static DataModelExtractor getInstance() {
        if (instance == null) {
            instance = new DataModelExtractor();
        }
        return instance;
    }

    public void getDataModel(String modelFileName) throws Exception {
        File file = new File(modelFileName);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();
        System.out.println("Root element "
            + doc.getDocumentElement().getNodeName());
        NodeList nodeLst = doc.getElementsByTagName("UML:Class");

        for (int s = 0; s < nodeLst.getLength(); s++) {

            Node fstNode = nodeLst.item(s);

            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                Element fstElmnt = (Element) fstNode;
                NodeList fstNmElmntLst = fstElmnt
                    .getElementsByTagName("UML:Classifier.feature");
                Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                NodeList fstNm = fstNmElmnt.getChildNodes();
                System.out.println("First Name : "
                    + ((Node) fstNm.item(0)).getNodeValue());
                NodeList lstNmElmntLst = fstElmnt
                    .getElementsByTagName("lastname");
                Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                NodeList lstNm = lstNmElmnt.getChildNodes();
                System.out.println("Last Name : "
                    + ((Node) lstNm.item(0)).getNodeValue());
            }

        }

    }
}
