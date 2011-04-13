package edu.ualberta.med.biobank.tools.validator;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.tools.modelumlparser.Attribute;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelUmlParser;
import edu.ualberta.med.biobank.tools.utils.CamelCase;

public class ValidatorGeneration {

    private static String USAGE = "Usage: validatorGeneration UMLFILE VALIDATORFILE";

    private static ValidatorGeneration instance = null;

    private static final Logger LOGGER = Logger
        .getLogger(ValidatorGeneration.class.getName());

    private static Properties xmlFileProperties;

    private static Properties validatorProperties;

    public static String getXmlFileString(String key, Object... args)
        throws IOException {
        if (xmlFileProperties == null) {
            xmlFileProperties = new java.util.Properties();
            xmlFileProperties.load(ValidatorGeneration.class
                .getResourceAsStream("xmlFileStrings.properties"));
        }
        return getString(xmlFileProperties, key, args);
    }

    private static String getString(Properties properties, String key,
        Object... args) {
        String pattern = null;
        try {
            pattern = properties.getProperty(key);
        } catch (MissingResourceException e) {
            pattern = '!' + key + '!';
        }
        if ((pattern != null) && (args != null) && (args.length > 0)) {
            return MessageFormat.format(pattern, args);
        }
        return pattern;
    }

    public static String getValidatorNameString(String key) throws IOException {
        if (validatorProperties == null) {
            validatorProperties = new java.util.Properties();
            validatorProperties.load(ValidatorGeneration.class
                .getResourceAsStream("validators.properties"));
        }
        return getString(validatorProperties, key);
    }

    private Map<String, ModelClass> modelClasses;

    private Map<String, ModelClass> dmTables;

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

    public static void main(String argv[]) throws Exception {
        ValidatorGeneration.getInstance().doWork(parseCommandLine(argv));
    }

    public void doWork(AppArgs appArgs) throws Exception {
        LOGGER.info("UML file: " + appArgs.modelFileName);
        LOGGER.info("validator file:  " + appArgs.validatorFileName);

        modelClasses = ModelUmlParser.getInstance().geLogicalModel(
            appArgs.modelFileName);
        dmTables = ModelUmlParser.getInstance().geDataModel(
            appArgs.modelFileName);
        ModelUmlParser.getInstance().geDataModel(appArgs.modelFileName);
        createValidatorFile(appArgs.validatorFileName);
    }

    private void createValidatorFile(String validatorFileName) throws Exception {
        File f = new File(validatorFileName);
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(getXmlFileString("file.begin").getBytes());

        for (ModelClass clazz : modelClasses.values()) {
            Map<String, Attribute> tableAttributes = new HashMap<String, Attribute>();
            ModelClass currentModelClass = clazz;
            while (currentModelClass != null) {
                String tableName = CamelCase.toTitleCase(currentModelClass
                    .getName());
                try {
                    tableAttributes
                        .putAll(dmTables.get(tableName).getAttrMap());
                } catch (Exception ex) {
                }
                currentModelClass = currentModelClass.getExtendsClass();
            }
            List<Attribute> attrList = new ArrayList<Attribute>();
            boolean hasErrors = false;
            for (Attribute attr : clazz.getAttrMap().values()) {
                Attribute tableAttribute = tableAttributes.get(CamelCase
                    .toTitleCase(attr.getName()));
                if (tableAttribute == null)
                    throw new Exception("No table attribute for "
                        + attr.getName() + " of class " + clazz.getName());
                Set<String> classAttrSter = attr.getStereotypes();
                Set<String> tableAttrSter = tableAttribute.getStereotypes();
                if ((classAttrSter.size() != tableAttrSter.size())
                    || !classAttrSter.containsAll(tableAttrSter)) {
                    LOGGER
                        .error(clazz.getName()
                            + ": not same stereotypes between logical model and data model"
                            + " for attribute " + attr.getName() + "\n");
                    hasErrors = true;
                } else if (classAttrSter.size() > 0) {
                    if ((classAttrSter.size() != 1)
                        || !classAttrSter.contains("unique"))
                        attrList.add(attr);
                }
            }
            if (hasErrors)
                throw new Exception("Problems with attribute. See above errors");
            if (attrList.size() > 0)
                fos.write(getXmlFileString("entry.begin",
                    clazz.getPkg() + "." + clazz.getName()).getBytes());
            for (Attribute attr : attrList) {
                fos.write(getXmlFileString("property.begin", attr.getName())
                    .getBytes());
                for (String stereotype : attr.getStereotypes()) {
                    int valueIndex = stereotype.indexOf("[");
                    String value = null;
                    if (valueIndex > -1) {
                        value = stereotype.substring(valueIndex + 1,
                            stereotype.indexOf("]"));
                        stereotype = stereotype.substring(0, valueIndex);
                    }
                    String validatorName = getValidatorNameString(stereotype);
                    if (validatorName != null) {
                        fos.write(getXmlFileString("validator.begin",
                            validatorName).getBytes());
                        if (value != null) {
                            fos.write(getXmlFileString("validator.value", value)
                                .getBytes());
                        }
                        fos.write(getXmlFileString("validator.end",
                            validatorName).getBytes());
                    }
                }
                fos.write(getXmlFileString("property.end").getBytes());
            }
            if (attrList.size() > 0)
                fos.write(getXmlFileString("entry.end").getBytes());
        }
        fos.write(getXmlFileString("file.end").getBytes());
        fos.flush();
        fos.close();
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
