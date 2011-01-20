package edu.ualberta.med.biobank.tools.logicalmodel;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.net.URISyntaxException;

import edu.ualberta.med.biobank.tools.ModelUmlParser;

public class LmExtractor {

    private static String USAGE = "Usage: lmextractor UMLFILE OUTDIR";

    private static LmExtractor instance = null;

    private AppArgs appArgs = null;

    private LmExtractor() {

    }

    public static LmExtractor getInstance() {
        if (instance == null) {
            instance = new LmExtractor();
        }
        return instance;
    }

    public static void main(String argv[]) {
        try {
            LmExtractor.getInstance().doWork(parseCommandLine(argv));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doWork(AppArgs appArgs) {
        this.appArgs = appArgs;

        if (appArgs.verbose) {
            System.out.println("  UML file: " + appArgs.modelFileName);
            System.out.println("  output dir:  " + appArgs.outDir);
            System.out.println();
        }

        try {
            ModelUmlParser.getInstance().geLogicalModel(appArgs.modelFileName);

            if (appArgs.verbose) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        createSourceCode();

    }

    private void createSourceCode() {
        // TODO Auto-generated method stub

    }

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
        if (args.length != 2) {
            System.out.println("Error: invalid arguments\n" + USAGE);
            System.exit(-1);
        }

        appArgs.modelFileName = args[0];
        appArgs.outDir = args[1];

        return appArgs;
    }

}

class AppArgs {
    boolean verbose = false;
    String modelFileName = null;
    String outDir = null;
}
