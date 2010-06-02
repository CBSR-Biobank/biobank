package edu.ualberta.med.biobank.strfields;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrFields {

    private static String USAGE = "Usage: strfields [options] UMLFILE HBMDIR OUTPATH\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    private static String HBM_FILE_EXTENSION = ".hbm.xml";

    private static Pattern HBM_STRING_ATTR = Pattern.compile(
    // "<property.*type=\"string\"\\s*column=\"([^\"]*)\"/>",
        "property", Pattern.CASE_INSENSITIVE);

    private static Pattern HBM_STRING_LENGTH_ATTR = Pattern.compile(
        "length=\"(\\d+)\"", Pattern.CASE_INSENSITIVE);
    private AppArgs appArgs;

    public StrFields(AppArgs appArgs) {
        this.appArgs = appArgs;

        try {
            DataModelExtractor.getInstance().getModel(appArgs.modelFileName);

            if (appArgs.verbose) {
                for (String className : DataModelExtractor.getInstance()
                    .getDmClassSet()) {
                    Map<String, String> attrMap = DataModelExtractor
                        .getInstance().getDmClassAttrMap(className);
                    for (String attrName : attrMap.keySet()) {
                        String type = attrMap.get(attrName);
                        if (!type.startsWith("VARCHAR"))
                            continue;

                        System.out.println(className + "." + attrName + ": "
                            + type);
                    }
                }
            }

            for (String file : getHbmFiles(appArgs.hbmDir)) {
                updateHbmFile(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void updateHbmFile(String hbmFileName) throws Exception {
        String className = toTitleCase(hbmFileName.replace(HBM_FILE_EXTENSION,
            ""));

        try {
            Map<String, String> attrMap = DataModelExtractor.getInstance()
                .getDmClassAttrMap(className);

            File inFile = new File(appArgs.hbmDir + "/" + hbmFileName);
            File outFile = File.createTempFile(className, HBM_FILE_EXTENSION);

            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            // BufferedWriter writer = new BufferedWriter(new
            // FileWriter(outFile));

            String line = reader.readLine();

            while (line != null) {
                System.out.println(hbmFileName + ": " + line);
                Matcher stringAttrMatcher = HBM_STRING_ATTR.matcher(line);
                if (stringAttrMatcher.find()) {
                    System.out.println("**** found : " + line);
                }

                // writer.write(line);
                // writer.newLine();
                line = reader.readLine();
            }

        } catch (Exception e) {
            if (appArgs.verbose) {
                System.out.println("class " + className
                    + " does not have a corresponding HBM file");
            }
        }

    }

    public static void main(String argv[]) {
        try {
            new StrFields(parseCommandLine(argv));
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
        appArgs.outDir = args[2];

        return appArgs;
    }

    @SuppressWarnings("unused")
    private String convertToCamelCase(String str) {
        StringBuffer sb = new StringBuffer();
        String[] splitStr = str.split("_");
        boolean firstTime = true;
        for (String temp : splitStr) {
            if (firstTime) {
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
    String outDir = null;
}
