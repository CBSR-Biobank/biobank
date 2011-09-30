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

public class BeanModifier {

    private static final Logger LOGGER = Logger.getLogger(HbmModifier.class
        .getName());

    private static Pattern BEAN_SERIAL_VERSION_DECL = Pattern
        .compile("private static final long serialVersionUID");
    private static final String LAST_UPDATE_DECL = "        private Integer version;";

    private static Pattern IMPLEMENTS_SERIALIZABLE_PATTERN = Pattern
        .compile("^public .* implements Serializable$");
    private static String SERIALIZABLE_TEXT_TO_REPLACE = "Serializable";
    private static String IMPLEMENTS_NEW_TEXT = "IBiobankModel";

    private static Pattern GETID_FIELD_PATTERN = Pattern
        .compile("^\\s*public Integer getId\\(\\)\\{$");

    private static Pattern SETID_FIELD_PATTERN = Pattern
        .compile("^\\s*public void setId\\(Integer id\\)\\{$");

    private static Pattern SETTER_IMPL_PATTERN = Pattern
        .compile("^\\s*this.id = id;$");

    private static BeanModifier instance = null;

    private BeanModifier() {

    }

    public static BeanModifier getInstance() {
        if (instance == null) {
            instance = new BeanModifier();
        }
        return instance;
    }

    public void alterBean(String filename, String className) throws Exception {
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
                } else {
                    // set ids Serializable instances instead of Integer
                    Matcher setidMatcher = SETID_FIELD_PATTERN.matcher(line);
                    if (setidMatcher.matches()) {
                        alteredLine = line.replaceFirst("Integer",
                            "Serializable");
                    } else {
                        // getId overrides the interfece getter but return an
                        // Integer
                        Matcher getidMatcher = GETID_FIELD_PATTERN
                            .matcher(line);
                        if (getidMatcher.matches()) {
                            alteredLine = "    @Override\n" + line;
                        } else {
                            // a cast need to be done on the id
                            Matcher setterMatcher = SETTER_IMPL_PATTERN
                                .matcher(line);
                            if (setterMatcher.matches()) {
                                alteredLine = line.replaceFirst("id;",
                                    "(Integer)id;");
                            }
                        }
                    }
                }
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
