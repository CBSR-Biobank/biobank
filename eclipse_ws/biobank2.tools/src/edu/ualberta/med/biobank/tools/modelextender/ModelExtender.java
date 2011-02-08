package edu.ualberta.med.biobank.tools.modelextender;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelUmlParser;

public class ModelExtender {

    private static String USAGE = "Usage: bbpeerbuilder UMLFILE OUTDIR";

    private static String PEER_PACKAGE = "edu.ualberta.med.biobank.common.peer";

    private static String WRAPPER_BASE_PACKAGE = "edu.ualberta.med.biobank.common.wrappers.base";

    private static final Logger LOGGER = Logger.getLogger(ModelExtender.class
        .getName());

    private static ModelExtender instance = null;

    Map<String, ModelClass> modelClasses;

    private ModelExtender() {

    }

    public static ModelExtender getInstance() {
        if (instance == null) {
            instance = new ModelExtender();
        }
        return instance;
    }

    public static void main(String argv[]) {
        try {
            ModelExtender.getInstance().doWork(parseCommandLine(argv));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doWork(final AppArgs appArgs) {
        LOGGER.info("UML file: " + appArgs.modelFileName);
        LOGGER.info("output dir:  " + appArgs.outDir);

        try {
            modelClasses = ModelUmlParser.getInstance().geLogicalModel(
                appArgs.modelFileName);

            if (!appArgs.wrapperBase) {
                new PeerBuilder(appArgs.outDir, PEER_PACKAGE, modelClasses)
                    .generateFiles();
            }

            if (!appArgs.wrapperBase) {
                new BaseWrapperBuilder(appArgs.outDir, WRAPPER_BASE_PACKAGE,
                    PEER_PACKAGE, modelClasses).generateFiles();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static AppArgs parseCommandLine(String argv[])
        throws URISyntaxException {
        AppArgs appArgs = new AppArgs();

        CmdLineParser parser = new CmdLineParser();
        Option wrapperBaseOpt = parser.addBooleanOption('w', "wrapperbase");
        Option verboseOpt = parser.addBooleanOption('v', "verbose");

        try {
            parser.parse(argv);
        } catch (OptionException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        Boolean wrapperBase = (Boolean) parser.getOptionValue(wrapperBaseOpt);
        if (wrapperBase != null) {
            appArgs.wrapperBase = wrapperBase.booleanValue();
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
    String modelFileName = null;
    String outDir = null;
    boolean wrapperBase = false;
    boolean verbose = false;
}
