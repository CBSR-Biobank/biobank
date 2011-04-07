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

    private static String USAGE = "Usage: bbpeerbuilder UMLFILE PEER_DIR WRAPPER_DIR";

    private static String PEER_PACKAGE = "edu.ualberta.med.biobank.common.peer";

    private static String WRAPPER_BASE_PACKAGE = "edu.ualberta.med.biobank.common.wrappers.base";

    private static final Logger LOGGER = Logger.getLogger(ModelExtender.class
        .getName());

    private static ModelExtender instance = null;

    private Map<String, ModelClass> modelClasses;

    private ModelExtender() {

    }

    public static ModelExtender getInstance() {
        if (instance == null) {
            instance = new ModelExtender();
        }
        return instance;
    }

    public static void main(String argv[]) throws Exception {
        ModelExtender.getInstance().doWork(parseCommandLine(argv));
    }

    public void doWork(final AppArgs appArgs) throws Exception {
        LOGGER.info("UML file: " + appArgs.modelFileName);
        LOGGER.info("peer class output dir:  " + appArgs.peerOutDir);
        LOGGER.info("wrapper base class output dir:  "
            + appArgs.wrapperBaseOutDir);

        modelClasses = ModelUmlParser.getInstance().geLogicalModel(
            appArgs.modelFileName);

        new PeerBuilder(appArgs.peerOutDir, PEER_PACKAGE, modelClasses)
            .generateFiles();

        new BaseWrapperBuilder(appArgs.wrapperBaseOutDir, WRAPPER_BASE_PACKAGE,
            PEER_PACKAGE, modelClasses).generateFiles();
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
        if (args.length != 3) {
            System.out.println("Error: invalid arguments\n" + USAGE);
            System.exit(-1);
        }

        appArgs.modelFileName = args[0];
        appArgs.peerOutDir = args[1];
        appArgs.wrapperBaseOutDir = args[2];

        return appArgs;
    }

}

class AppArgs {
    String modelFileName = null;
    String peerOutDir = null;
    String wrapperBaseOutDir = null;
    boolean verbose = false;
}
