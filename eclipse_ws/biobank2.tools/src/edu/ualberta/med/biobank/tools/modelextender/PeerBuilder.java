package edu.ualberta.med.biobank.tools.modelextender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import edu.ualberta.med.biobank.tools.modelumlparser.Attribute;
import edu.ualberta.med.biobank.tools.modelumlparser.ClassAssociation;
import edu.ualberta.med.biobank.tools.modelumlparser.ClassAssociationType;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;
import edu.ualberta.med.biobank.tools.utils.CamelCase;

public class PeerBuilder {

    private static final Logger LOGGER = Logger.getLogger(PeerBuilder.class
        .getName());

    private final String outputdir;

    private final String packagename;

    public PeerBuilder(final String outputdir, final String packagename,
        final Map<String, ModelClass> modelClasses) throws IOException {
        this.outputdir = outputdir;
        this.packagename = packagename;

        File f = new File(outputdir);
        if (!f.exists()) {
            f.mkdir();
        }

        for (ModelClass mc : modelClasses.values()) {
            createPeerClass(mc);
        }
    }

    private void createPeerClass(ModelClass mc) throws IOException {
        Map<String, Integer> importCount = new HashMap<String, Integer>();

        LOGGER.info("generating peer class for " + mc.getName());
        File f = new File(outputdir + "/" + mc.getName() + "Peer.java");
        FileOutputStream fos = new FileOutputStream(f);

        StringBuffer sb = new StringBuffer("package ").append(packagename)
            .append(";\n").append("\nimport ")
            .append(TypeReference.class.getName()).append(";\n")
            .append("import ").append(Collections.class.getName())
            .append(";\n").append("import ").append(Property.class.getName())
            .append(";\n").append("import ").append(List.class.getName())
            .append(";\n").append("import ").append(ArrayList.class.getName())
            .append(";\n");

        // add imports for required classes
        importCount.clear();
        for (Attribute attr : mc.getAttrMap().values()) {
            String attrType = attr.getType();
            if (importCount.get(attrType) != null) {
                // already added an import for this class
                continue;
            }

            importCount.put(attrType, 1);
            if (attrType.equals("Date")) {
                sb.append("import ").append(Date.class.getName()).append(";\n");
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

        // import the model class itself
        if (!importCount.containsKey(mc.getName())) {
            sb.append("import ").append(mc.getPkg()).append(".")
                .append(mc.getName()).append(";\n");
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
                .append(mc.getAttrMap().get(attr).getType()).append(", ")
                .append(mc.getName()).append("> ")
                .append(CamelCase.toTitleCase(attr))
                .append(" = Property.create(\"").append(attr)
                .append("\", new TypeReference<")
                .append(mc.getAttrMap().get(attr).getType())
                .append(">() {});\n\n");
        }

        // Association property fields
        for (String assocName : mc.getAssocMap().keySet()) {
            ClassAssociation assoc = mc.getAssocMap().get(assocName);
            if ((assoc.getAssociationType() == ClassAssociationType.ZERO_OR_ONE_TO_MANY)
                || (assoc.getAssociationType() == ClassAssociationType.ONE_TO_MANY)) {
                sb.append("   public static final Property<Collection<")
                    .append(assoc.getToClass().getName()).append(">, ")
                    .append(mc.getName()).append("> ")
                    .append(CamelCase.toTitleCase(assocName))
                    .append(" = Property.create(\"").append(assocName)
                    .append("\", new TypeReference<Collection<")
                    .append(assoc.getToClass().getName())
                    .append(">>() {});\n\n");
            } else {
                sb.append("   public static final Property<")
                    .append(assoc.getToClass().getName()).append(", ")
                    .append(mc.getName()).append("> ")
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
                .append("      List<String> aList = new ArrayList<String>();\n");
            if (ec != null) {
                sb.append("      aList.addAll(").append(ec.getName())
                    .append("Peer.PROP_NAMES").append(");\n");
            }

            for (String attr : mc.getAttrMap().keySet()) {
                sb.append("      aList.add(\"").append(attr).append("\");\n");
            }
            for (String assocName : mc.getAssocMap().keySet()) {
                sb.append("      aList.add(\"").append(assocName)
                    .append("\");\n");
            }
            sb.append(
                "      PROP_NAMES = Collections.unmodifiableList(aList);\n")
                .append("   };\n");
        }

        sb.append("}\n");
        fos.write(sb.toString().getBytes());
    }

}
