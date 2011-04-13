package edu.ualberta.med.biobank.tools.hbmpostproc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.tools.modelumlparser.Attribute;

public class HbmModifier {

    private static final Logger LOGGER = Logger.getLogger(HbmModifier.class
        .getName());

    private static Pattern HBM_ATTR_MAPPING_COMMENT_TAG = Pattern.compile(
        "<!-- Attributes mapping for the .+ class -->",
        Pattern.CASE_INSENSITIVE);

    private static Pattern HBM_STRING_ATTR = Pattern.compile(
        "<property.*type=\"string\"\\s*column=\"([^\"]*)\"/>",
        Pattern.CASE_INSENSITIVE);

    private static Pattern HBM_ATTR = Pattern.compile(
        "<property.*column=\"([^\"]*)\"/>", Pattern.CASE_INSENSITIVE);

    private static String HBM_FILE_EXTENSION = ".hbm.xml";

    private static final String TIMESTAMP_PROPERTY = "<timestamp name=\"lastModifyDateTime\" column=\"LAST_MODIFIY_DATE_TIME\" access=\"field\" />";

    private static HbmModifier instance = null;

    private boolean documentChanged = false;

    private HbmModifier() {

    }

    public static HbmModifier getInstance() {
        if (instance == null) {
            instance = new HbmModifier();
        }
        return instance;
    }

    public void alterMapping(String filename, String className,
        String tableName, Map<String, Attribute> columnTypeMap,
        Set<String> uniqueList, Set<String> notNullList) throws Exception {
        if (!filename.contains(className)) {
            throw new Exception(
                "HBM file name does not contain class name: filename "
                    + filename + ", classname " + className);
        }

        try {
            File outFile = File.createTempFile(className, HBM_FILE_EXTENSION);

            BufferedReader reader = new BufferedReader(new FileReader(filename));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

            String line = reader.readLine();

            boolean idPropertyFound = false;

            while (line != null) {
                String alteredLine = new String(line);
                Matcher idMatcher = HBM_ATTR_MAPPING_COMMENT_TAG.matcher(line);
                Matcher stringAttrMatcher = HBM_STRING_ATTR.matcher(line);
                Matcher attrMatcher = HBM_ATTR.matcher(line);

                if (!idPropertyFound && idMatcher.find()) {
                    // has to be after discriminator tag and before first
                    // property tag
                    alteredLine = new StringBuffer(TIMESTAMP_PROPERTY)
                        .append("\n").append(alteredLine).toString();
                    idPropertyFound = true;
                } else if (stringAttrMatcher.find()
                    && !line.contains("length=\"")) {
                    String attrName = stringAttrMatcher.group(1);
                    Attribute attr = columnTypeMap.get(attrName);

                    if (attr == null) {
                        throw new Exception("column not found in column map: "
                            + attrName);
                    }

                    alteredLine = fixStringAttributes(line, className, attr);
                    alteredLine = addContraints(alteredLine, attrName,
                        uniqueList, notNullList);
                    documentChanged |= !line.equals(alteredLine);
                } else if (attrMatcher.find()) {
                    String attrName = attrMatcher.group(1);
                    alteredLine = addContraints(alteredLine, attrName,
                        uniqueList, notNullList);
                }

                writer.write(alteredLine);
                writer.newLine();
                line = reader.readLine();
            }

            reader.close();
            writer.flush();
            writer.close();

            if (!idPropertyFound) {
                throw new Exception(
                    "tag not found found for inserting timestamp in HBM file "
                        + filename);
            }

            if (documentChanged) {
                FileUtils.copyFile(outFile, new File(filename));
                if (HbmPostProcess.getInstance().getVerbose()) {
                    System.out.println("HBM Modified: " + filename);
                }
            }

            outFile.deleteOnExit();
        } catch (IOException e) {
            System.out.println("class " + className
                + " does not have a corresponding HBM file");
        }
    }

    private String fixStringAttributes(String line, String className,
        Attribute attr) {
        boolean lineChanged = false;

        Integer attrLen = attr.getLength();

        if (attrLen != null) {
            line = line.replace("type=\"string\"", "type=\"" + attr.getType()
                + "\" length=\"" + attr.getLength() + "\"");
            lineChanged = true;
        } else {
            line = line.replace("type=\"string\"", "type=\"" + attr.getType()
                + "\"");
            lineChanged = true;
        }

        if (lineChanged) {
            LOGGER.debug("line changed: " + className + ": " + line);
        }
        return line;

    }

    private String addContraints(String line, String attrName,
        Set<String> uniqueList, Set<String> notNullList) {
        String s = "";
        if (uniqueList.contains(attrName) && !s.contains("unique="))
            s += " unique=\"true\"";
        if (notNullList.contains(attrName) && !s.contains("not-null="))
            s += " not-null=\"true\"";
        if (s.length() > 0) {
            documentChanged = true;
            return line.replace("/>", s + "/>");
        }
        return line;
    }
}
