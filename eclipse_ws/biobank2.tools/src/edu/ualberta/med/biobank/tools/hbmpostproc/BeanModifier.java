package edu.ualberta.med.biobank.tools.hbmpostproc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;

public class BeanModifier {

    private static final Logger LOGGER = Logger.getLogger(HbmModifier.class
        .getName());

    private static Pattern BEAN_SERIAL_VERSION_DECL = Pattern
        .compile("private static final long serialVersionUID");
    private static final String LAST_UPDATE_DECL =
        "        private Integer version;";

    private static Pattern IMPLEMENTS_SERIALIZABLE_PATTERN = Pattern
        .compile("^public .* implements Serializable$");
    private static String SERIALIZABLE_TEXT_TO_REPLACE = "Serializable";
    private static String IMPLEMENTS_NEW_TEXT = "IBiobankModel";

    private static Pattern COLLECTION_VARIABLE = Pattern
        .compile("^(\\s*)private Collection<(\\w+)> (\\w+);$");

    private static BeanModifier instance = null;

    private BeanModifier() {

    }

    public static BeanModifier getInstance() {
        if (instance == null) {
            instance = new BeanModifier();
        }
        return instance;
    }

    public void alterBean(String filename, ModelClass modelClass)
        throws Exception {
        String className = modelClass.getName();
        if (!filename.contains(className)) {
            throw new Exception(
                "Bean file name does not contain class name: filename "
                    + filename + ", classname " + className);
        }

        File outFile = File.createTempFile(className, ".java");

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

        String line = reader.readLine();
        boolean documentChanged = false;

        while (line != null) {
            String alteredLine = new String(line);

            if (modelClass.getExtendsClass() == null) {
                // this applies to base classes only
                //
                // add the version field
                Matcher declMatcher = BEAN_SERIAL_VERSION_DECL.matcher(line);
                if (declMatcher.find()) {
                    alteredLine = new StringBuffer(alteredLine).append("\n\n")
                        .append(LAST_UPDATE_DECL).toString();
                } else {
                    // implements IBiobankModel interface
                    Matcher implMatcher = IMPLEMENTS_SERIALIZABLE_PATTERN
                        .matcher(line);
                    if (implMatcher.matches()) {
                        alteredLine = line.replaceFirst(
                            SERIALIZABLE_TEXT_TO_REPLACE, IMPLEMENTS_NEW_TEXT);
                    }
                }
            }

            // initialize collections
            if (line.equals("import java.util.Collection;")) {
                alteredLine = line + "\nimport java.util.HashSet;";
            }
            Matcher m = COLLECTION_VARIABLE.matcher(line);
            if (m.matches()) {
                String space = m.group(1);
                String type = m.group(2);
                String variableName = m.group(3);
                alteredLine =
                    space + "private Collection<" + type + "> "
                        + variableName
                        + " = new HashSet<" + type + ">();\n";
            }

            documentChanged |= !line.equals(alteredLine);

            writer.write(alteredLine);
            writer.newLine();
            line = reader.readLine();
        }

        reader.close();
        writer.flush();
        writer.close();

        if (documentChanged) {
            FileUtils.copyFile(outFile, new File(filename));
            if (HbmPostProcess.getInstance().getVerbose()) {
                LOGGER.info("Bean Modified: " + filename);
            }
        }

    }
}
