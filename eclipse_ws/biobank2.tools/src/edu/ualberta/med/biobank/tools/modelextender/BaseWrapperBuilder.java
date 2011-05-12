package edu.ualberta.med.biobank.tools.modelextender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.tools.modelumlparser.Attribute;
import edu.ualberta.med.biobank.tools.modelumlparser.ClassAssociation;
import edu.ualberta.med.biobank.tools.modelumlparser.ClassAssociationType;
import edu.ualberta.med.biobank.tools.modelumlparser.ModelClass;
import edu.ualberta.med.biobank.tools.utils.CamelCase;

public class BaseWrapperBuilder extends BaseBuilder {

    private static final Logger LOGGER = Logger
        .getLogger(BaseWrapperBuilder.class.getName());

    private static final String SUPPRESS_WARNING_UNCHECKED = "@SuppressWarnings(\"unchecked\")";

    protected final String peerpackagename;

    protected final String wrapperPackageName;

    protected final String internalWrapperPackageName;

    protected Map<String, ModelClass> modelBaseClasses;

    protected Map<String, String> wrapperMap;

    public BaseWrapperBuilder(String outputdir, String packagename,
        String peerpackagename, Map<String, ModelClass> modelClasses) {
        super(outputdir, packagename, modelClasses);
        this.peerpackagename = peerpackagename;

        wrapperPackageName = packagename.replace(".base", "");
        internalWrapperPackageName = packagename.replace(".base", ".internal");
    }

    @Override
    public void generateFiles() throws Exception {
        // find all base classes
        modelBaseClasses = new HashMap<String, ModelClass>();
        for (Entry<String, ModelClass> e : modelClasses.entrySet()) {
            ModelClass ec = e.getValue().getExtendsClass();
            if (ec != null) {
                modelBaseClasses.put(ec.getName(), ec);
            }
        }

        wrapperMap = new HashMap<String, String>();

        populateWrapperPackageNameMap(outputdir, wrapperPackageName);
        populateWrapperPackageNameMap(outputdir + "/internal",
            internalWrapperPackageName);

        super.generateFiles();
    }

    private Map<String, String> populateWrapperPackageNameMap(String dirname,
        String packagename) throws Exception {

        // get list of internal wrappers
        File dir = new File(dirname);
        if (!dir.exists()) {
            throw new Exception("directory does not exist " + dirname);
        }

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("Wrapper.java");
            }
        };

        // remove ".java"
        for (String filename : dir.list(filter)) {
            wrapperMap.put(filename.replace(".java", ""), packagename);
        }
        return wrapperMap;
    }

    @Override
    protected void generateClassFile(ModelClass mc) throws Exception {
        String className = mc.getName();
        String wrapperName = new StringBuilder(className).append("Wrapper")
            .toString();

        if (!wrapperMap.containsKey(wrapperName)) {
            LOGGER.info("skipping generation of wrapper for " + className
                + ": no wrapper found with name " + wrapperName);
            return;
        }

        LOGGER.info("generating wrapper base class for " + mc.getName());

        File f = new File(outputdir + "/base/" + mc.getName()
            + "BaseWrapper.java");
        FileOutputStream fos = new FileOutputStream(f);

        StringBuilder contents = new StringBuilder("/*\n")
            .append(
                " * This code is automatically generated. Please do not edit.\n")
            .append(" */\n\n")
            .append("package ")
            .append(packagename)
            .append(";\n\n")
            .append("import ")
            .append(List.class.getName())
            .append(";\n")
            .append(
                "import gov.nih.nci.system.applicationservice.WritableApplicationService;\n")
            .append("import ").append(mc.getPkg()).append(".")
            .append(mc.getName()).append(";\n").append("import ")
            .append(Property.class.getName()).append(";\n");

        if (mc.getExtendsClass() == null)
            contents.append("import ").append(wrapperPackageName)
                .append(".ModelWrapper;\n");

        if (modelBaseClasses.containsKey(mc.getName())
            || mc.getExtendsClass() != null)
            // need this for the getPropertyChangeNames method
            contents.append("import ").append(ArrayList.class.getName())
                .append(";\n");

        // import the peer class
        contents.append("import ").append(peerpackagename).append(".")
            .append(mc.getName()).append("Peer;\n");

        contents.append(getWrapperImports(mc));

        if (modelBaseClasses.containsKey(mc.getName())) {
            contents.append("\npublic abstract class ").append(mc.getName())
                .append("BaseWrapper").append("<E extends ")
                .append(mc.getName()).append("> extends ModelWrapper<E> ");
        } else if (mc.getExtendsClass() != null) {
            contents.append("\npublic abstract class ").append(mc.getName())
                .append("BaseWrapper").append(" extends ")
                .append(mc.getExtendsClass().getName()).append("Wrapper<")
                .append(mc.getName()).append("> ");
        } else {
            contents.append("\npublic class ").append(mc.getName())
                .append("BaseWrapper").append(" extends ModelWrapper<")
                .append(mc.getName()).append("> ");
        }
        contents.append("{\n\n");
        contents.append(createContructors(mc));
        contents.append(createRequiredMethods(mc));

        for (Attribute attr : mc.getAttrMap().values()) {
            if (attr.getName().equals("id"))
                continue;
            contents.append(createPropertyGetter(mc, attr));
            contents.append(createPropertySetter(mc, attr));
        }

        for (ClassAssociation assoc : mc.getAssocMap().values()) {
            ClassAssociationType assocType = assoc.getAssociationType();

            if ((assocType == ClassAssociationType.ZERO_OR_ONE_TO_ONE)
                || (assocType == ClassAssociationType.ONE_TO_ONE)) {
                contents.append(createWrappedPropertyGetter(mc, assoc));
                contents.append(createWrappedPropertySetter(mc, assoc));
            } else {
                contents.append(createCollectionGetter(mc, assoc));
                contents.append(createCollectionAdder(mc, assoc));
                contents.append(createCollectionRemover(mc, assoc));
                contents.append(createCollectionRemoverWithCheck(mc, assoc));
            }
        }

        contents.append("}\n");
        fos.write(contents.toString().getBytes());
    }

    private String createContructors(ModelClass mc) {
        String wrappedObjectType = mc.getName();
        if (modelBaseClasses.containsKey(mc.getName())) {
            wrappedObjectType = "E";
        }

        StringBuilder result = new StringBuilder("    public ")
            .append(mc.getName())
            .append("BaseWrapper(WritableApplicationService appService) {\n")
            .append("        super(appService);\n").append("    }\n\n")
            .append("    public ").append(mc.getName())
            .append("BaseWrapper(WritableApplicationService appService,\n")
            .append("        ").append(wrappedObjectType)
            .append(" wrappedObject) {\n")
            .append("        super(appService, wrappedObject);\n")
            .append("    }\n\n");

        return result.toString();
    }

    private String createRequiredMethods(ModelClass mc) {
        StringBuilder result = new StringBuilder();

        if (!modelBaseClasses.containsKey(mc.getName())) {
            // wrappers for model base classes do not implement the
            // getWrappedClass() method
            result.append("    @Override\n    public final Class<")
                .append(mc.getName()).append("> getWrappedClass() {\n")
                .append("        return ").append(mc.getName())
                .append(".class;\n").append("    }\n\n");
        }

        String wrappedObjectType = mc.getName();
        if (modelBaseClasses.containsKey(mc.getName())) {
            wrappedObjectType = "E";
        }

        boolean hasBooleanAttributes = false;
        StringBuilder getNewObject = new StringBuilder();

        getNewObject.append("    @Override\n").append("   protected ")
            .append(wrappedObjectType)
            .append(" getNewObject() throws Exception {\n").append("        ")
            .append(wrappedObjectType)
            .append(" newObject = super.getNewObject();\n");

        // by default, set Boolean attributes to false when a new object is
        // constructed.
        for (Attribute attr : mc.getAttrMap().values()) {
            if (attr.getType().equals("Boolean")) {
                getNewObject.append("        newObject.set")
                    .append(CamelCase.toCamelCase(attr.getName(), true))
                    .append("(false);\n");
                hasBooleanAttributes = true;
            }
        }

        getNewObject.append("        return newObject;\n").append("    }\n\n");

        if (hasBooleanAttributes) {
            result.append(getNewObject);
        }

        result.append("    @Override\n")
            .append("   protected List<Property<?, ? super ")
            .append(wrappedObjectType).append(">> getProperties() {\n");

        if (modelBaseClasses.containsKey(mc.getName()))
            result
                .append("        return new ArrayList<Property<?, ? super E>>(")
                .append(mc.getName()).append("Peer.PROPERTIES);\n");
        else if (mc.getExtendsClass() == null)
            result.append("        return ").append(mc.getName())
                .append("Peer.PROPERTIES;\n");
        else
            result.append("        List<Property<?, ? super ")
                .append(mc.getName())
                .append(">> superNames = super.getProperties();\n")
                .append("        List<Property<?, ? super ")
                .append(mc.getName())
                .append(">> all = new ArrayList<Property<?, ? super ")
                .append(mc.getName()).append(">>();\n")
                .append("        all.addAll(superNames);\n")
                .append("        all.addAll(").append(mc.getName())
                .append("Peer.PROPERTIES);\n").append("        return all;\n");
        result.append("    }\n\n");

        return result.toString();
    }

    private String createPropertyGetter(ModelClass mc, Attribute member) {
        StringBuilder result = new StringBuilder();

        result.append("   public ").append(member.getType()).append(" get")
            .append(CamelCase.toCamelCase(member.getName(), true))
            .append("() {\n").append("      return getProperty(")
            .append(mc.getName()).append("Peer.")
            .append(CamelCase.toTitleCase(member.getName())).append(");\n")
            .append("   }\n\n");
        return result.toString();
    }

    private String createPropertySetter(ModelClass mc, Attribute member) {
        StringBuilder result = new StringBuilder();

        result.append("   public void set")
            .append(CamelCase.toCamelCase(member.getName(), true)).append("(")
            .append(member.getType()).append(" ").append(member.getName())
            .append(") {\n");

        String value = member.getName();
        if (member.getType().equals("String")) {
            value = "trimmed";
            result.append("      String ").append(value).append(" = ")
                .append(member.getName()).append(" == null ? null : ")
                .append(member.getName()).append(".trim();\n");
        }

        result.append("      setProperty(").append(mc.getName())
            .append("Peer.").append(CamelCase.toTitleCase(member.getName()))
            .append(", ").append(value).append(");\n").append("   }\n\n");

        return result.toString();
    }

    private String createWrappedPropertyGetter(ModelClass mc,
        ClassAssociation assoc) throws Exception {
        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_OR_ONE_TO_ONE)
            && (assocType != ClassAssociationType.ONE_TO_ONE)) {
            throw new Exception("class " + mc.getName() + " does not have a "
                + "zero to one or one to one relationship with class"
                + assoc.getClass());
        }
        String assocClassName = assoc.getToClass().getName();
        String assocName = assoc.getAssocName();
        StringBuilder result = new StringBuilder();

        String genericString = "";
        if (modelBaseClasses.containsKey(assocClassName)) {
            genericString = "<?>";
            result.append("   ").append(SUPPRESS_WARNING_UNCHECKED)
                .append("\n");
        }
        result.append("   public ").append(assocClassName).append("Wrapper")
            .append(genericString).append(" get")
            .append(CamelCase.toCamelCase(assocName, true)).append("() {\n")
            .append("      return getWrappedProperty(").append(mc.getName())
            .append("Peer.").append(CamelCase.toTitleCase(assocName))
            .append(", ").append(assocClassName).append("Wrapper.class);\n")
            .append("   }\n\n");
        return result.toString();
    }

    private String createWrappedPropertySetter(ModelClass mc,
        ClassAssociation assoc) throws Exception {
        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_OR_ONE_TO_ONE)
            && (assocType != ClassAssociationType.ONE_TO_ONE)) {
            throw new Exception("class " + mc.getName() + " does not have a "
                + "zero to one or one to one relationship with class"
                + assoc.getClass());
        }

        String assocClassName = assoc.getToClass().getName();
        String assocName = assoc.getAssocName();
        StringBuilder result = new StringBuilder();

        String genericString = "";
        if (modelBaseClasses.containsKey(assocClassName))
            genericString = "<?>";

        result.append("   public void set")
            .append(CamelCase.toCamelCase(assocName, true)).append("(")
            .append(assocClassName).append("Wrapper").append(genericString)
            .append(" ").append(assocName).append(") {\n")
            .append("      setWrappedProperty(").append(mc.getName())
            .append("Peer.").append(CamelCase.toTitleCase(assocName))
            .append(", ").append(assocName).append(");\n").append("   }\n\n");
        return result.toString();
    }

    private String createCollectionGetter(ModelClass mc, ClassAssociation assoc)
        throws Exception {
        String assocClassName = assoc.getToClass().getName();
        String assocWrapperName = new StringBuilder(assocClassName).append(
            "Wrapper").toString();

        if (!wrapperMap.containsKey(assocWrapperName))
            return "";

        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_OR_ONE_TO_MANY)
            && (assocType != ClassAssociationType.ONE_TO_MANY)) {
            throw new Exception("class " + mc.getName() + " does not have a "
                + "zero to many or one to many relationship with class"
                + assoc.getClass());
        }

        String assocName = assoc.getAssocName();
        StringBuilder result = new StringBuilder();

        // fix warnings
        if (mc.getName().equals("ShippingMethod")) {
            result
                .append("   @SuppressWarnings({ \"unchecked\", \"rawtypes\" })\n");
        }

        result.append("   public List<").append(assocClassName)
            .append("Wrapper> get")
            .append(CamelCase.toCamelCase(assocName, true))
            .append("(boolean sort) {\n")
            .append("      return getWrapperCollection(").append(mc.getName())
            .append("Peer.").append(CamelCase.toTitleCase(assocName))
            .append(", ").append(assocClassName)
            .append("Wrapper.class, sort);\n").append("   }\n\n");
        return result.toString();
    }

    private Object createCollectionAdder(ModelClass mc, ClassAssociation assoc)
        throws Exception {
        String assocClassName = assoc.getToClass().getName();
        String assocWrapperName = new StringBuilder(assocClassName).append(
            "Wrapper").toString();

        if (!wrapperMap.containsKey(assocWrapperName))
            return "";

        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_OR_ONE_TO_MANY)
            && (assocType != ClassAssociationType.ONE_TO_MANY)) {
            throw new Exception("class " + mc.getName() + " does not have a "
                + "zero to many or one to many relationship with class"
                + assoc.getClass());
        }

        String assocName = assoc.getAssocName();
        StringBuilder result = new StringBuilder();

        // fix warnings
        if (mc.getName().equals("ShippingMethod")) {
            result
                .append("   @SuppressWarnings({ \"unchecked\", \"rawtypes\" })\n");
        }

        result.append("   public void addTo")
            .append(CamelCase.toCamelCase(assocName, true)).append("(List<")
            .append(assocClassName).append("Wrapper> ").append(assocName)
            .append(") {\n").append("      addToWrapperCollection(")
            .append(mc.getName()).append("Peer.")
            .append(CamelCase.toTitleCase(assocName)).append(", ")
            .append(assocName).append(");\n").append("   }\n\n");
        return result.toString();
    }

    private Object createCollectionRemover(ModelClass mc, ClassAssociation assoc)
        throws Exception {
        String assocClassName = assoc.getToClass().getName();
        String assocWrapperName = new StringBuilder(assocClassName).append(
            "Wrapper").toString();

        if (!wrapperMap.containsKey(assocWrapperName))
            return "";

        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_OR_ONE_TO_MANY)
            && (assocType != ClassAssociationType.ONE_TO_MANY)) {
            throw new Exception("class " + mc.getName() + " does not have a "
                + "zero to many or one to many relationship with class"
                + assoc.getClass());
        }

        String assocName = assoc.getAssocName();
        StringBuilder result = new StringBuilder();

        // fix warnings
        if (mc.getName().equals("ShippingMethod")) {
            result
                .append("   @SuppressWarnings({ \"unchecked\", \"rawtypes\" })\n");
        }

        result.append("   public void removeFrom")
            .append(CamelCase.toCamelCase(assocName, true)).append("(List<")
            .append(assocClassName).append("Wrapper> ").append(assocName)
            .append(") {\n").append("      removeFromWrapperCollection(")
            .append(mc.getName()).append("Peer.")
            .append(CamelCase.toTitleCase(assocName)).append(", ")
            .append(assocName).append(");\n").append("   }\n\n");
        return result.toString();
    }

    private Object createCollectionRemoverWithCheck(ModelClass mc,
        ClassAssociation assoc) throws Exception {
        String assocClassName = assoc.getToClass().getName();
        String assocWrapperName = new StringBuilder(assocClassName).append(
            "Wrapper").toString();

        if (!wrapperMap.containsKey(assocWrapperName))
            return "";

        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_OR_ONE_TO_MANY)
            && (assocType != ClassAssociationType.ONE_TO_MANY)) {
            throw new Exception("class " + mc.getName() + " does not have a "
                + "zero to many or one to many relationship with class"
                + assoc.getClass());
        }

        String assocName = assoc.getAssocName();
        StringBuilder result = new StringBuilder();

        // fix warnings
        if (mc.getName().equals("ShippingMethod")) {
            result
                .append("   @SuppressWarnings({ \"unchecked\", \"rawtypes\" })\n");
        }

        result.append("   public void removeFrom")
            .append(CamelCase.toCamelCase(assocName, true))
            .append("WithCheck(List<").append(assocClassName)
            .append("Wrapper> ").append(assocName)
            .append(") throws BiobankCheckException {\n")
            .append("      removeFromWrapperCollectionWithCheck(")
            .append(mc.getName()).append("Peer.")
            .append(CamelCase.toTitleCase(assocName)).append(", ")
            .append(assocName).append(");\n").append("   }\n\n");
        return result.toString();
    }

    private String getWrapperImports(ModelClass mc) {
        Map<String, Integer> importCount = new HashMap<String, Integer>();
        StringBuilder sb = new StringBuilder();

        // add import for base wrapper
        ModelClass ec = mc.getExtendsClass();
        if (ec != null) {
            StringBuilder wrapperName = new StringBuilder(ec.getName())
                .append("Wrapper");

            String packagename = wrapperMap.get(wrapperName.toString());

            if (packagename != null) {
                importCount.put(ec.getName(), 1);
                sb.append("import ").append(packagename).append(".")
                    .append(wrapperName).append(";\n");
            }
        }

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

        Map<String, ClassAssociation> assocMap = mc.getAssocMap();
        for (ClassAssociation assoc : assocMap.values()) {
            ModelClass toClass = assoc.getToClass();

            // check if need to import BioBankCheckException
            ClassAssociationType assocType = assoc.getAssociationType();
            if (((assocType == ClassAssociationType.ZERO_OR_ONE_TO_MANY) || (assocType == ClassAssociationType.ONE_TO_MANY))
                && (importCount.get("BioBankCheckException") == null)) {

                String wrapperName = new StringBuilder(toClass.getName())
                    .append("Wrapper").toString();
                if (wrapperMap.get(wrapperName) != null) {

                    importCount.put("BioBankCheckException", 1);
                    sb.append("import edu.ualberta.med.biobank.common.exception.BiobankCheckException;\n");
                }
            }

            if (importCount.get(toClass.getName()) != null) {
                // already added an import for this class
                continue;
            }

            StringBuilder wrapperName = new StringBuilder(toClass.getName())
                .append("Wrapper");

            String packagename = wrapperMap.get(wrapperName.toString());

            if (packagename == null) {
                // this wrapper does not exist
                continue;
            }

            importCount.put(toClass.getName(), 1);

            sb.append("import ").append(packagename).append(".")
                .append(wrapperName).append(";\n");
        }

        return sb.toString();
    }

}
