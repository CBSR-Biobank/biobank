package edu.ualberta.med.biobank.tools.strfields;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class HbmModifier {

    private static Pattern HBM_STRING_ATTR = Pattern.compile(
        "<property.*type=\"string\"\\s*column=\"([^\"]*)\"/>",
        Pattern.CASE_INSENSITIVE);

    private static String HBM_FILE_EXTENSION = ".hbm.xml";

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
        String tableName, Map<String, Integer> columnLenMap) throws Exception {
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
            while (line != null) {
                Matcher stringAttrMatcher = HBM_STRING_ATTR.matcher(line);
                if (stringAttrMatcher.find() && !line.contains("length=\"")) {
                    String attrName = stringAttrMatcher.group(1);
                    Integer attrLen = columnLenMap.get(attrName);

                    if (attrLen != null) {
                        line = line.replace("type=\"string\"",
                            "type=\"string\" length=\"" + attrLen + "\"");
                        documentChanged = true;
                    }
                }

                writer.write(line);
                writer.newLine();
                line = reader.readLine();
            }

            reader.close();
            writer.flush();
            writer.close();

            if (documentChanged) {
                FileUtils.copyFile(outFile, new File(filename));
                if (StrFields.getInstance().getVerbose()) {
                    System.out.println("HBM Modified: " + filename);
                }
            }

            outFile.deleteOnExit();
        } catch (Exception e) {
            System.out.println("class " + className
                + " does not have a corresponding HBM file");
        }
    }
}
