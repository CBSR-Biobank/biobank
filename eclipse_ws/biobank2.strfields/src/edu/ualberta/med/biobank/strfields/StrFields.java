package edu.ualberta.med.biobank.strfields;

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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrFields {

    private static String USAGE = "Usage: strfields [options] UMLFILE HBMDIR TEMPLATE\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    private static StrFields instance = null;

    private static String HBM_FILE_EXTENSION = ".hbm.xml";

    private static Pattern VARCHAR_LEN = Pattern.compile("VARCHAR\\((\\d+)\\)");

    private AppArgs appArgs = null;

    private StrFields() {

    }

    public static StrFields getInstance() {
        if (instance == null) {
            instance = new StrFields();
        }
        return instance;
    }

    public void doWork(AppArgs appArgs) {
        this.appArgs = appArgs;

        if (appArgs.verbose) {
            System.out.println("  UML file: " + appArgs.modelFileName);
            System.out.println("  HBM dir:  " + appArgs.hbmDir);
            System.out.println("  Template: " + appArgs.template);
            System.out.println();
        }

        try {
            DataModelExtractor.getInstance().getModel(appArgs.modelFileName);

            if (appArgs.verbose) {
                for (String className : DataModelExtractor.getInstance()
                    .getDmClassSet()) {
                    Map<String, String> attrMap = DataModelExtractor
                        .getInstance().getDmClassAttrMap(className);
                    for (String attrName : attrMap.keySet()) {
                        String type = attrMap.get(attrName);
                        if (!type.startsWith("VARCHAR")
                            && !type.startsWith("TEXT"))
                            continue;

                        System.out.println("  " + className + "." + attrName
                            + ": " + type);
                    }
                }
                System.out.println();
            }

            for (String file : getHbmFiles(appArgs.hbmDir)) {
                updateHbmFile(file);
            }

            createVarCharLengthsSourceCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            System.out.println("Error: no files found in directory " + hbmDir);
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
        String className = toTitleCase(hbmFileName.replace(HBM_FILE_EXTENSION,
            ""));

        Map<String, String> attrMap = DataModelExtractor.getInstance()
            .getDmClassAttrMap(className);
        Map<String, Integer> attrLengthMap = new HashMap<String, Integer>();

        for (String attrName : attrMap.keySet()) {
            String attrType = attrMap.get(attrName);

            if (attrType.startsWith("VARCHAR")) {
                Matcher varcharMatcher = VARCHAR_LEN.matcher(attrType);

                if (varcharMatcher.find()) {
                    attrLengthMap.put(attrName, Integer.valueOf(varcharMatcher
                        .group(1)));
                }
            } else if (attrType.startsWith("TEXT")) {
                attrLengthMap.put(attrName, 500);
            }
        }

        HbmModifier.getInstance().alterMapping(hbmFilePath, attrLengthMap);
    }

    private void createVarCharLengthsSourceCode() throws Exception {
        String newLine = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();

        for (String className : DataModelExtractor.getInstance()
            .getDmClassSet()) {
            Map<String, String> attrMap = DataModelExtractor.getInstance()
                .getDmClassAttrMap(className);
            for (String attrName : attrMap.keySet()) {
                String attrType = attrMap.get(attrName);
                if (!attrType.startsWith("VARCHAR"))
                    continue;

                if (attrType.startsWith("VARCHAR")) {
                    Matcher varcharMatcher = VARCHAR_LEN.matcher(attrType);

                    if (varcharMatcher.find()) {
                        attrType = varcharMatcher.group(1);

                        sb
                            .append("\t\taMap.put(\"edu.ualberta.med.biobank.model."
                                + toCamelCase(className + "." + attrName, true)
                                + "\", " + attrType + ");");
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

    public static void main(String argv[]) {
        try {
            StrFields.getInstance().doWork(parseCommandLine(argv));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        Boolean verbose = (Boolean) parser.getOptionValue(verboseOpt);
        if (verbose != null) {
            appArgs.verbose = verbose.booleanValue();
        }

        String[] args = parser.getRemainingArgs();
        if (args.length != 3) {
            System.out.println("Error: invalid arguments\n" + USAGE);
            System.exit(-1);
        }

        appArgs.modelFileName = args[0];
        appArgs.hbmDir = args[1];
        appArgs.template = args[2];

        return appArgs;
    }

    private String toCamelCase(String str, boolean firstCharUpperCase) {
        StringBuffer sb = new StringBuffer();
        String[] splitStr = str.split("_");
        boolean firstTime = true;
        for (String temp : splitStr) {
            if (firstTime && !firstCharUpperCase) {
                sb.append(temp.toLowerCase());
                firstTime = false;
            } else {
                sb.append(Character.toUpperCase(temp.charAt(0)));
                sb.append(temp.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    private String toTitleCase(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = str.length(); i < n; ++i) {
            if (i == 0) {
                sb.append(str.charAt(0));
                continue;
            }

            char ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                sb.append("_" + ch);
            } else {
                sb.append(Character.toUpperCase(ch));
            }
        }

        return sb.toString();
    }
}

class AppArgs {
    boolean verbose = false;
    String modelFileName = null;
    String hbmDir = null;
    String template = null;
}
