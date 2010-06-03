package edu.ualberta.med.biobank.strfields;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HbmModifier {

    private static HbmModifier instance = null;

    private Document doc;

    private boolean documentChanged = false;

    private HbmModifier() {

    }

    public static HbmModifier getInstance() {
        if (instance == null) {
            instance = new HbmModifier();
        }
        return instance;
    }

    public void alterMapping(String filename, Map<String, Integer> columnLenMap)
        throws Exception {
        File file = new File(filename);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(file);
        doc.getDocumentElement().normalize();

        NodeList nodeLst = doc.getElementsByTagName("property");
        for (int i = 0, n = nodeLst.getLength(); i < n; i++) {
            Node node = nodeLst.item(i);

            Node parent = node.getParentNode();
            if (parent == null) {
                throw new Exception("property node does not have a parent");
            }

            String parentName = ((Element) parent).getAttribute("name");
            if (!filename.contains(parentName)) {
                throw new Exception("HBM mapping class does not match filename");
            }

            Element el = (Element) node;

            String columnName = el.getAttribute("column");
            String attrType = el.getAttribute("type");
            if ((columnName == null) || (attrType == null)) {
                throw new Exception("bad format for HBM mapping property ");
            }

            if (!attrType.equals("string")) {
                // only looking for strings
                continue;
            }

            String attrLength = el.getAttribute("length");
            Integer newLength = columnLenMap.get(columnName);
            if (((attrLength == null) || (attrLength.length() == 0))
                && (newLength != null)) {
                el.setAttribute("length", newLength.toString());
                documentChanged = true;
            }
        }

        if (documentChanged) {
            Source source = new DOMSource(doc);
            Result result = new StreamResult(new File(filename));
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(source, result);

            if (StrFields.getInstance().getVerbose()) {
                System.out.println("HBM Modified: " + filename);
            }
        }
    }

}
