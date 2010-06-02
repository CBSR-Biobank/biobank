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
        NodeList pkgNodeLst = doc.getElementsByTagName("UML:Package");

        for (int s = 0; s < pkgNodeLst.getLength(); s++) {
            Element el = (Element) pkgNodeLst.item(s);
            if (!el.getAttribute("name").equals("Data Model"))
                continue;

            NodeList dmNodeLst = el.getChildNodes();

            for (int i = 0, n = dmNodeLst.getLength(); i < n; ++i) {
                Node dmChildren = (Node) dmNodeLst.item(i);
                if (!dmNodeLst.item(i).getNodeName().equals(
                    "UML:Namespace.ownedElement"))
                    continue;

                NodeList opNodeList = dmChildren.getChildNodes();

                for (int j = 0, m = opNodeList.getLength(); j < m; ++j) {
                    Node opNodeChildList = (Node) opNodeList.item(j);
                    if (!opNodeChildList.getNodeName().equals("UML:Class"))
                        continue;

                    System.out.println("node " + opNodeChildList.getNodeName()
                        + " "
                        + ((Element) opNodeChildList).getAttribute("name"));
                }

            }
        }

    }
}
