package edu.ualberta.med.biobank.strfields;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();

        // dissable parsing of external DTD since this slows down the processing
        db.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(java.lang.String publicId,
                java.lang.String systemId) throws SAXException,
                java.io.IOException {
                if (systemId.endsWith(".dtd"))
                    // this deactivates all DTDs by giving empty XML docs
                    return new InputSource(new ByteArrayInputStream(
                        "<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
                else
                    return null;
            }
        });

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
            Transformer t = TransformerFactory.newInstance().newTransformer();

            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t
                .setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                    "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
            t.transform(new DOMSource(doc),
                new StreamResult(new File(filename)));

            if (StrFields.getInstance().getVerbose()) {
                System.out.println("HBM Modified: " + filename);
            }
        }
    }

}
