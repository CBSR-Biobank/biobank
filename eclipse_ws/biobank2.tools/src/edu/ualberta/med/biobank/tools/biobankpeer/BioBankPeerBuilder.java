package edu.ualberta.med.biobank.tools.biobankpeer;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelUmlParser;
import edu.ualberta.med.biobank.tools.utils.CamelCase;

public class BioBankPeerBuilder {

    private static String USAGE = "Usage: lmextractor UMLFILE OUTDIR";

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

        for (ModelClass mc : modelClasses.values()) {
            LOGGER.info("generating peer class for " + mc.getName());
            f = new File(appArgs.outDir + "/" + mc.getName() + "Peer.java");
            FileOutputStream fos = new FileOutputStream(f);

            StringBuffer sb = new StringBuffer("package ").append(PACKAGE)
                .append(";\n\n");
            sb.append("import edu.ualberta.med.biobank.common.util.TypeReference;\n");
            sb.append("import edu.ualberta.med.biobank.common.util.Property;\n\n");
            sb.append("public class ").append(mc.getName()).append("Peer ");

            ModelClass ec = mc.getExtendsClass();

            if (ec != null) {
                sb.append(" extends ").append(ec.getName()).append("Peer ");
            }

            sb.append("{\n");

            // Member property names
            for (String attr : mc.getAttrMap().keySet()) {
                sb.append("   public static final String ")
                    .append(CamelCase.toTitleCase(attr)).append(" = \"")
                    .append(attr).append("\";\n\n");
            }

            // Associated member property names
            for (String assoc : mc.getAssocMap().keySet()) {
                sb.append("   public static final String ")
                    .append(CamelCase.toTitleCase(assoc)).append(" = \"")
                    .append(assoc).append("\";\n\n");
            }

            // Member property fields
            for (String attr : mc.getAttrMap().keySet()) {
                sb.append("   //public static final Property<")
                    .append(mc.getAttrMap().get(attr)).append("> PROP_")
                    .append(CamelCase.toTitleCase(attr))
                    .append(" = Property.create(\"").append(attr)
                    .append("\", new TypeReference<")
                    .append(mc.getAttrMap().get(attr)).append(">());\n\n");
            }

            for (String assoc : mc.getAssocMap().keySet()) {
            }

            sb.append("}\n");
            fos.write(sb.toString().getBytes());
        }

    }

    private static AppArgs parseCommandLine(String argv[])
        throws URISyntaxException {
        AppArgs appArgs = new AppArgs();

        PropertyConfigurator.configure("conf/log4j.properties");

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
