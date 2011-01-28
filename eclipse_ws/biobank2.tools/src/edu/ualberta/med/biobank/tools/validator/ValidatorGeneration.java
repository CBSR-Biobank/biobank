package edu.ualberta.med.biobank.tools.validator;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.tools.modelumlparser.Attribute;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelUmlParser;
import edu.ualberta.med.biobank.tools.utils.CamelCase;

public class ValidatorGeneration {

    private static String USAGE = "Usage: validatorGeneration UMLFILE VALIDATORFILE";

    private static ValidatorGeneration instance = null;

    private static final Logger LOGGER = Logger
        .getLogger(ValidatorGeneration.class.getName());

    private Map<String, ModelClass> modelClasses;

    private Object dmClasses;

    public static class AppArgs {
        boolean verbose = false;
        String modelFileName = null;
        String validatorFileName = null;
    }

    public static ValidatorGeneration getInstance() {
        if (instance == null) {
            instance = new ValidatorGeneration();
        }
        return instance;
    }

    public static void main(String argv[]) {
        try {
            ValidatorGeneration.getInstance().doWork(parseCommandLine(argv));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doWork(AppArgs appArgs) {
        // LOGGER.info("UML file: " + appArgs.modelFileName);
        // LOGGER.info("validator file:  " + appArgs.validatorFileName);

        try {
            modelClasses = ModelUmlParser.getInstance().geLogicalModel(
                appArgs.modelFileName);
            ModelUmlParser.getInstance().geDataModel(appArgs.modelFileName);
            createValidatorFile(appArgs.validatorFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createValidatorFile(String validatorFileName) throws Exception {
        File f = new File(validatorFileName);
        StringBuffer errorMsgs = new StringBuffer();
        for (ModelClass clazz : modelClasses.values()) {
            String tableName = CamelCase.toTitleCase(clazz.getName());
            Map<String, Attribute> tableAttributes = null;
            try {
                tableAttributes = ModelUmlParser.getInstance()
                    .getDmTableAttrMap(tableName);
            } catch (Exception ex) {
                // class devrait savoir la table associee grace a la dependance
            }
            if (tableAttributes != null)
                for (Attribute attr : clazz.getAttrMap().values()) {
                    Attribute tableAttribute = tableAttributes.get(CamelCase
                        .toTitleCase(attr.getName()));
                    Set<String> classAttrSter = attr.getStereotypes();
                    Set<String> tableAttrSter = tableAttribute.getStereotypes();
                    if ((classAttrSter.size() != tableAttrSter.size())
                        || !classAttrSter.containsAll(tableAttrSter)) {
                        errorMsgs.append("Not same stereotypes between "
                            + clazz.getName() + " and " + tableName
                            + " for attribute " + attr.getName() + "\n");
                    }
                }
        }
        if (errorMsgs.length() > 0)
            throw new Exception("Problems in uml:\n" + errorMsgs.toString());
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
        appArgs.validatorFileName = args[1];

        return appArgs;
    }
}
