package edu.ualberta.med.biobank.tools.hbmpostproc;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.tools.modelumlparser.Attribute;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelUmlParser;
import edu.ualberta.med.biobank.tools.utils.CamelCase;

public class HbmPostProcess {
    private static final String NOT_EMPTY_STEREOTYPE = "not-empty";

    private static final String NOT_NULL_STEREOTYPE = "not-null";

    private static final String UNIQUE_STEREOTYPE = "unique";

    private static final Logger LOGGER = Logger.getLogger(HbmPostProcess.class
        .getName());

    private static String USAGE = "Usage: strfields [options] UMLFILE HBMDIR TEMPLATE\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    private static HbmPostProcess instance = null;

    private static String HBM_FILE_EXTENSION = ".hbm.xml";

    private static Pattern VARCHAR_LEN = Pattern.compile("VARCHAR\\((\\d+)\\)");

    private AppArgs appArgs = null;

    private Map<String, ModelClass> dmClasses;

    private Map<String, ModelClass> dmTables;

    private HbmPostProcess() {

    }

    public static HbmPostProcess getInstance() {
        if (instance == null) {
            instance = new HbmPostProcess();
        }
        return instance;
    }

    public void doWork(AppArgs appArgs) throws Exception {
        this.appArgs = appArgs;

        if (appArgs.verbose) {
            LOGGER.info("  UML file: " + appArgs.modelFileName);
            LOGGER.info("  HBM dir:  " + appArgs.hbmDir);
            LOGGER.info("  Template: " + appArgs.template);
        }

        dmClasses = ModelUmlParser.getInstance().geLogicalModel(
            appArgs.modelFileName);
        dmTables = ModelUmlParser.getInstance().geDataModel(
            appArgs.modelFileName);

        if (appArgs.verbose) {
            for (ModelClass dmTable : dmTables.values()) {
                Map<String, Attribute> attrMap = dmTable.getAttrMap();
                for (String attrName : attrMap.keySet()) {
                    String type = attrMap.get(attrName).getType();
                    if (!type.startsWith("VARCHAR") && !type.startsWith("TEXT"))
                        continue;

                    LOGGER.info("  " + dmTable + "." + attrName + ": " + type);
                }
            }
        }

        for (String file : getHbmFiles(appArgs.hbmDir)) {
            updateHbmFile(file);
        }

        createVarCharLengthsSourceCode();
    }

    public boolean getVerbose() throws Exception {
        if (appArgs == null) {
            throw new Exception("invalid state");
        }
        return appArgs.verbose;
    }

    /*
     * Returns all "*.hbm.xml" files found in directory hbmDir
     */
    private List<String> getHbmFiles(String hbmDir) {
        File dir = new File(hbmDir);
        String[] files = dir.list();
        if (files == null) {
            LOGGER.info("Error: no files found in directory " + hbmDir);
            System.exit(-1);
        }

        List<String> hbmFiles = new ArrayList<String>();
        for (String file : files) {
            if (!file.endsWith(HBM_FILE_EXTENSION))
                continue;
            hbmFiles.add(file);
        }
        return hbmFiles;
    }

    /*
     * Creates a new HBM file with lengths derived from UML file for all string
     * fields
     */
    private void updateHbmFile(String hbmFileName) throws Exception {
        String hbmFilePath = appArgs.hbmDir + "/" + hbmFileName;
        String className = hbmFileName.replace(HBM_FILE_EXTENSION, "");
        String tableName = CamelCase.toTitleCase(className);

        ModelClass table = dmTables.get(tableName);
        Map<String, Attribute> attrMap = table.getAttrMap();
        Map<String, Attribute> attrTypeMap = new HashMap<String, Attribute>();

        Set<String> uniqueList = new HashSet<String>();
        Set<String> notNullList = new HashSet<String>();

        for (String attrName : attrMap.keySet()) {
            Attribute attr = attrMap.get(attrName);

            String attrType = attr.getType();
            if (attrType.startsWith("VARCHAR")) {
                Matcher varcharMatcher = VARCHAR_LEN.matcher(attrType);

                if (varcharMatcher.find()) {
                    attrTypeMap.put(attrName, new Attribute(attrName, "string",
                        Integer.valueOf(varcharMatcher.group(1))));
                }
            } else if (attrType.startsWith("TEXT")) {
                attrTypeMap
                    .put(attrName, new Attribute(attrName, "text", null));
            }

            if (attr.hasStereotype(UNIQUE_STEREOTYPE))
                uniqueList.add(attrName);
            if (attr.hasStereotype(NOT_NULL_STEREOTYPE))
                notNullList.add(attrName);
            if (attr.hasStereotype(NOT_EMPTY_STEREOTYPE))
                notNullList.add(attrName);
        }

        HbmModifier.getInstance().alterMapping(hbmFilePath, className,
            tableName, attrTypeMap, uniqueList, notNullList);
    }

    private void createVarCharLengthsSourceCode() throws Exception {
        String newLine = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();

        for (ModelClass dmClass : dmClasses.values()) {
            LOGGER.debug("class name: " + dmClass.getName());

            ModelClass dmTable = null;
            ModelClass extendsClass = dmClass.getExtendsClass();

            if (extendsClass == null) {
                dmTable = dmTables
                    .get(CamelCase.toTitleCase(dmClass.getName()));
            } else {
                dmTable = dmTables.get(CamelCase.toTitleCase(extendsClass
                    .getName()));
            }

            if (dmTable == null) {
                LOGGER.info("Error: data model table not found for class "
                    + dmClass.getName());
                System.exit(-1);
            }

            Map<String, Attribute> dmClassAttrMap = new HashMap<String, Attribute>(
                dmClass.getAttrMap());
            Map<String, Attribute> dmTableAttrMap = dmTable.getAttrMap();

            if (extendsClass != null) {
                // add all attributes from the super class, not already in
                // derived class
                for (Entry<String, Attribute> entry : extendsClass.getAttrMap()
                    .entrySet()) {
                    if (!dmClassAttrMap.keySet().contains(entry.getKey())) {
                        dmClassAttrMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            for (String attrName : dmClassAttrMap.keySet()) {
                String tableAttrName = CamelCase.toTitleCase(attrName);
                String attrType = dmTableAttrMap.get(tableAttrName).getType();

                LOGGER.debug("class name: " + dmClass.getName() + ", attr: "
                    + attrName + ", attrType: " + attrType);

                if (!dmTableAttrMap.containsKey(tableAttrName)) {
                    continue;
                }

                if (!attrType.startsWith("VARCHAR"))
                    continue;

                if (attrType.startsWith("VARCHAR")) {
                    Matcher varcharMatcher = VARCHAR_LEN.matcher(attrType);

                    if (varcharMatcher.find()) {
                        attrType = varcharMatcher.group(1);

                        sb.append(
                            "\t\taMap.put(\"edu.ualberta.med.biobank.model.")
                            .append(dmClass.getName()).append(".")
                            .append(attrName).append("\", ").append(attrType)
                            .append(");");
                        sb.append(newLine);
                    }
                }
            }
        }

        String content = sb.toString();

        BufferedReader reader = new BufferedReader(new FileReader(
            appArgs.template));
        BufferedWriter writer = new BufferedWriter(new FileWriter(
            appArgs.template.replace(".template", "")));

        String line = reader.readLine();
        while (line != null) {
            line = line.replace("{attrMapContents}", content);
            writer.write(line);
            writer.newLine();
            line = reader.readLine();
        }

        writer.flush();
        writer.close();
        reader.close();
    }

    public static void main(String argv[]) throws Exception {
        HbmPostProcess.getInstance().doWork(parseCommandLine(argv));
    }

    /*
     * Parses the command line arguments and returns them in an AppArgs object.
     */
    private static AppArgs parseCommandLine(String argv[])
        throws URISyntaxException {
        AppArgs appArgs = new AppArgs();

        CmdLineParser parser = new CmdLineParser();
        Option verboseOpt = parser.addBooleanOption('v', "verbose");

        try {
            parser.parse(argv);
        } catch (OptionException e) {
            LOGGER.info(e.getMessage());
            System.exit(-1);
        }

        Boolean verbose = (Boolean) parser.getOptionValue(verboseOpt);
        if (verbose != null) {
            appArgs.verbose = verbose.booleanValue();
        }

        String[] args = parser.getRemainingArgs();
        if (args.length != 3) {
            LOGGER.info("Error: invalid arguments\n" + USAGE);
            System.exit(-1);
        }

        appArgs.modelFileName = args[0];
        appArgs.hbmDir = args[1];
        appArgs.template = args[2];

        return appArgs;
    }
}

class AppArgs {
    boolean verbose = false;
    String modelFileName = null;
    String hbmDir = null;
    String template = null;
}
