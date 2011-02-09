package edu.ualberta.med.biobank.tools.modelextender;

import java.io.File;
import java.util.Map;

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

    public void generateFiles() throws Exception {
        File f = new File(outputdir);
        if (!f.exists()) {
            f.mkdir();
        }

        for (ModelClass mc : modelClasses.values()) {
            generateClassFile(mc);
        }
    }

    protected abstract void generateClassFile(ModelClass mc) throws Exception;

}
