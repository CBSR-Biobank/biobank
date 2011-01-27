package edu.ualberta.med.biobank.tools.biobankpeer;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.tools.modelumlparser.ClassAssociation;
import edu.ualberta.med.biobank.tools.modelumlparser.ClassAssociationType;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelUmlParser;
import edu.ualberta.med.biobank.tools.utils.CamelCase;

public class BioBankPeerBuilder {

    private static String USAGE = "Usage: bbpeerbuilder UMLFILE OUTDIR";

    private static String PACKAGE = "edu.ualberta.med.biobank.common.peer";

    private static final Logger LOGGER = Logger
        .getLogger(BioBankPeerBuilder.class.getName());

    private static BioBankPeerBuilder instance = null;

    Map<String, ModelClass> modelClasses;

    private AppArgs appArgs = null;

    private BioBankPeerBuilder() {

    }

    public static BioBankPeerBuilder getInstance() {
        if (instance == null) {
            instance = new BioBankPeerBuilder();
        }
        return instance;
    }

    public static void main(String argv[]) {
        try {
            // PropertyConfigurator.configure("conf/log4j.properties");
            BioBankPeerBuilder.getInstance().doWork(parseCommandLine(argv));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doWork(AppArgs appArgs) {
        this.appArgs = appArgs;

        LOGGER.info("UML file: " + appArgs.modelFileName);
        LOGGER.info("output dir:  " + appArgs.outDir);

        try {
            modelClasses = ModelUmlParser.getInstance().geLogicalModel(
                appArgs.modelFileName);
            createSourceCode();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createSourceCode() throws Exception {
        File f = new File(appArgs.outDir);
        if (!f.exists()) {
            f.mkdir();
        }

        Map<String, Integer> importCount = new HashMap<String, Integer>();

        for (ModelClass mc : modelClasses.values()) {
            LOGGER.info("generating peer class for " + mc.getName());
            f = new File(appArgs.outDir + "/" + mc.getName() + "Peer.java");
            FileOutputStream fos = new FileOutputStream(f);

            StringBuffer sb = new StringBuffer("package ").append(PACKAGE)
                .append(";\n").append("\nimport ")
                .append(TypeReference.class.getName()).append(";\n")
                .append("import ").append(Collections.class.getName())
                .append(";\n").append("import ")
                .append(Property.class.getName()).append(";\n")
                .append("import ").append(List.class.getName()).append(";\n")
                .append("import ").append(ArrayList.class.getName())
                .append(";\n");

            // add imports for required classes
            importCount.clear();
            for (String attrType : mc.getAttrMap().values()) {
                if (importCount.get(attrType) != null) {
                    // already added an import for this class
                    continue;
                }

                importCount.put(attrType, 1);
                if (attrType.equals("Date")) {
                    sb.append("import ").append(Date.class.getName())
                        .append(";\n");
                }
            }

            boolean hasCollections = false;
            Map<String, ClassAssociation> assocMap = mc.getAssocMap();
            for (ClassAssociation assoc : assocMap.values()) {
                ModelClass toClass = assoc.getToClass();

                if ((assoc.getAssociationType() == ClassAssociationType.ZERO_OR_ONE_TO_MANY)
                    || (assoc.getAssociationType() == ClassAssociationType.ONE_TO_MANY)) {
                    hasCollections = true;
                }

                if (importCount.get(toClass.getName()) != null) {
                    // already added an import for this class
                    continue;
                }

                importCount.put(toClass.getName(), 1);
                sb.append("import ").append(toClass.getPkg()).append(".")
                    .append(toClass.getName()).append(";\n");
            }

            if (hasCollections) {
                sb.append("import ").append(Collection.class.getName())
                    .append(";\n");
            }

            sb.append("\npublic class ").append(mc.getName()).append("Peer ");
            ModelClass ec = mc.getExtendsClass();
            if (ec != null) {
                sb.append(" extends ").append(ec.getName()).append("Peer ");
            }
            sb.append("{\n");

            // Member property fields
            for (String attr : mc.getAttrMap().keySet()) {
                sb.append("   public static final Property<")
                    .append(mc.getAttrMap().get(attr)).append("> ")
                    .append(CamelCase.toTitleCase(attr))
                    .append(" = Property.create(\"").append(attr)
                    .append("\", new TypeReference<")
                    .append(mc.getAttrMap().get(attr)).append(">() {});\n\n");
            }

            // Association property fields
            for (String assocName : mc.getAssocMap().keySet()) {
                ClassAssociation assoc = mc.getAssocMap().get(assocName);
                if ((assoc.getAssociationType() == ClassAssociationType.ZERO_OR_ONE_TO_MANY)
                    || (assoc.getAssociationType() == ClassAssociationType.ONE_TO_MANY)) {
                    sb.append("   public static final Property<Collection<")
                        .append(assoc.getToClass().getName()).append(">> ")
                        .append(CamelCase.toTitleCase(assocName))
                        .append(" = Property.create(\"").append(assocName)
                        .append("\", new TypeReference<Collection<")
                        .append(assoc.getToClass().getName())
                        .append(">>() {});\n\n");
                } else {
                    sb.append("   public static final Property<")
                        .append(assoc.getToClass().getName()).append("> ")
                        .append(CamelCase.toTitleCase(assocName))
                        .append(" = Property.create(\"").append(assocName)
                        .append("\", new TypeReference<")
                        .append(assoc.getToClass().getName())
                        .append(">() {});\n\n");
                }
            }

            // property change names
            if (mc.getAttrMap().size() + mc.getAssocMap().size() > 0) {
                sb.append("   public static final List<String> PROP_NAMES;\n")
                    .append("   static {\n")
                    .append(
                        "      List<String> aList = new ArrayList<String>();\n");
                if (ec != null) {
                    sb.append("      aList.addAll(").append(ec.getName())
                        .append("Peer.PROP_NAMES").append(");\n");
                }

                for (String attr : mc.getAttrMap().keySet()) {
                    sb.append("      aList.add(\"").append(attr)
                        .append("\");\n");
                }
                for (String assocName : mc.getAssocMap().keySet()) {
                    sb.append("      aList.add(\"").append(assocName)
                        .append("\");\n");
                }
                sb.append(
                    "      PROP_NAMES = Collections.unmodifiableList(aList);\n")
                    .append("   }");
            }

            sb.append("}\n");
            fos.write(sb.toString().getBytes());
        }

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
