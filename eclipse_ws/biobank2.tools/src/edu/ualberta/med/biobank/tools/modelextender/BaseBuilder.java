package edu.ualberta.med.biobank.tools.modelextender;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.tools.modelumlparser.Attribute;
import edu.ualberta.med.biobank.tools.modelumlparser.ClassAssociation;
import edu.ualberta.med.biobank.tools.modelumlparser.ClassAssociationType;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;

public abstract class BaseBuilder {

    protected final String outputdir;

    protected final String packagename;

    protected final Map<String, ModelClass> modelClasses;

    public BaseBuilder(final String outputdir, final String packagename,
        final Map<String, ModelClass> modelClasses) {
        this.outputdir = outputdir;
        this.packagename = packagename;
        this.modelClasses = modelClasses;
    }

    public void generateFiles() throws IOException {
        File f = new File(outputdir);
        if (!f.exists()) {
            f.mkdir();
        }

        for (ModelClass mc : modelClasses.values()) {
            generateClassFile(mc);
        }
    }

    protected abstract void generateClassFile(ModelClass mc) throws IOException;

    protected String getImports(ModelClass mc) {
        Map<String, Integer> importCount = new HashMap<String, Integer>();

        StringBuilder sb = new StringBuilder();

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

        if (hasCollections) {
            sb.append("import ").append(Collection.class.getName())
                .append(";\n");
        }

        // import the model class itself
        if (!importCount.containsKey(mc.getName())) {
            sb.append("import ").append(mc.getPkg()).append(".")
                .append(mc.getName()).append(";\n");
        }

        return sb.toString();
    }

}
