package edu.ualberta.med.biobank.tools.modelextender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

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

    protected Set<String> imports = new TreeSet<String>();

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

        StringBuilder preClassDef = new StringBuilder("/*\n")
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
            .append("import ")
            .append(mc.getPkg())
            .append(".")
            .append(mc.getName())
            .append(";\n")
            .append(
                "import edu.ualberta.med.biobank.common.wrappers.Property;\n");

        if (mc.getExtendsClass() == null)
            preClassDef.append("import ").append(wrapperPackageName)
                .append(".ModelWrapper;\n");

        if (modelBaseClasses.containsKey(mc.getName())
            || mc.getExtendsClass() != null)
            // need this for the getPropertyChangeNames method
            preClassDef.append("import ").append(ArrayList.class.getName())
                .append(";\n");

        // import the peer class
        preClassDef.append("import ").append(peerpackagename).append(".")
            .append(mc.getName()).append("Peer;\n");

        preClassDef.append(getWrapperImports(mc));

        StringBuilder classDef = new StringBuilder();

        if (modelBaseClasses.containsKey(mc.getName())) {
            classDef.append("\npublic abstract class ").append(mc.getName())
                .append("BaseWrapper").append("<E extends ")
                .append(mc.getName()).append("> extends ModelWrapper<E> ");
        } else if (mc.getExtendsClass() != null) {
            classDef.append("\npublic abstract class ").append(mc.getName())
                .append("BaseWrapper").append(" extends ")
                .append(mc.getExtendsClass().getName()).append("Wrapper<")
                .append(mc.getName()).append("> ");
        } else {
            classDef.append("\npublic class ").append(mc.getName())
                .append("BaseWrapper").append(" extends ModelWrapper<")
                .append(mc.getName()).append("> ");
        }
        classDef.append("{\n\n");
        classDef.append(createContructors(mc));
        classDef.append(createRequiredMethods(mc));

        for (Attribute attr : mc.getAttrMap().values()) {
            if (attr.getName().equals("id"))
                continue;
            classDef.append(createPropertyGetter(mc, attr));
            classDef.append(createPropertySetter(mc, attr));
        }

        for (ClassAssociation assoc : mc.getAssocMap().values()) {
            ClassAssociationType assocType = assoc.getAssociationType();

            if ((assocType == ClassAssociationType.ZERO_TO_ONE)
                || (assocType == ClassAssociationType.ONE_TO_ONE)) {
                classDef.append(createWrappedPropertyGetter(mc, assoc));
                classDef.append(createWrappedPropertySetter(mc, assoc, false));
                classDef.append(createWrappedPropertySetter(mc, assoc, true));
            } else {
                classDef.append(createCollectionGetter(mc, assoc));
                classDef.append(createCollectionAdder(mc, assoc, false));
                classDef.append(createCollectionAdder(mc, assoc, true));
                classDef.append(createCollectionRemover(mc, assoc, false));
                classDef.append(createCollectionRemover(mc, assoc, true));
                classDef.append(createCollectionRemoverWithCheck(mc, assoc,
                    false));
                classDef.append(createCollectionRemoverWithCheck(mc, assoc,
                    true));
            }
        }

        classDef.append("}\n");

        StringBuilder contents = new StringBuilder();
        contents.append(preClassDef);

        for (String imp : imports) {
            contents.append("import ").append(imp).append(";\n");
        }
        imports.clear();

        contents.append(classDef);
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
        String idModelClassName = mc.getName();
        if (modelBaseClasses.containsKey(mc.getName())) {
            idModelClassName = modelBaseClasses.get(mc.getName()).getName();
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
            .append("    public Property<Integer, ? super ")
            .append(wrappedObjectType).append("> getIdProperty() {\n")
            .append("        return ").append(idModelClassName)
            .append("Peer.ID;\n").append("    }\n\n");

        result.append("    @Override\n")
            .append("    protected List<Property<?, ? super ")
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

        result.append("    public ").append(member.getType()).append(" get")
            .append(CamelCase.toCamelCase(member.getName(), true))
            .append("() {\n").append("        return getProperty(")
            .append(mc.getName()).append("Peer.")
            .append(CamelCase.toTitleCase(member.getName())).append(");\n")
            .append("    }\n\n");
        return result.toString();
    }

    private String createPropertySetter(ModelClass mc, Attribute member) {
        StringBuilder result = new StringBuilder();

        result.append("    public void set")
            .append(CamelCase.toCamelCase(member.getName(), true)).append("(")
            .append(member.getType()).append(" ").append(member.getName())
            .append(") {\n");

        String value = member.getName();
        if (member.getType().equals("String")) {
            value = "trimmed";
            result.append("        String ").append(value).append(" = ")
                .append(member.getName()).append(" == null ? null : ")
                .append(member.getName()).append(".trim();\n");
        }

        result.append("        setProperty(").append(mc.getName())
            .append("Peer.").append(CamelCase.toTitleCase(member.getName()))
            .append(", ").append(value).append(");\n").append("    }\n\n");

        return result.toString();
    }

    private String createWrappedPropertyGetter(ModelClass mc,
        ClassAssociation assoc) throws Exception {
        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_TO_ONE)
            && (assocType != ClassAssociationType.ONE_TO_ONE)) {
            throw new Exception("class " + mc.getName() + " does not have a "
                + "zero to one or one to one relationship with class"
                + assoc.getClass());
        }
        String assocClassName = assoc.getToClass().getName();
        String assocName = assoc.getAssocName();
        StringBuilder result = new StringBuilder();

        String inverse = createWrappedPropertyGetterInverse(mc, assoc);

        String peerProperty = mc.getName() + "Peer."
            + CamelCase.toTitleCase(assocName);
        String notCached = "";
        if (!inverse.isEmpty()) {
            notCached = "        boolean notCached = !isPropertyCached("
                + peerProperty + ");\n";
        }

        String genericString = "";
        if (modelBaseClasses.containsKey(assocClassName)) {
            genericString = "<?>";
            result.append("   ").append(SUPPRESS_WARNING_UNCHECKED)
                .append("\n");
        }

        result.append("    public ").append(assocClassName).append("Wrapper")
            .append(genericString).append(" get")
            .append(CamelCase.toCamelCase(assocName, true)).append("() {\n")
            .append(notCached).append("        ").append(assocClassName)
            .append("Wrapper ").append(genericString).append(assocName)
            .append(" = getWrappedProperty(").append(peerProperty).append(", ")
            .append(assocClassName).append("Wrapper.class);\n").append(inverse)
            .append("        return ").append(assocName).append(";\n")
            .append("    }\n\n");
        return result.toString();
    }

    private String createWrappedPropertySetter(ModelClass mc,
        ClassAssociation assoc, boolean isInternal) throws Exception {
        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_TO_ONE)
            && (assocType != ClassAssociationType.ONE_TO_ONE)) {
            throw new Exception("class " + mc.getName() + " does not have a "
                + "zero to one or one to one relationship with class"
                + assoc.getClass());
        }

        String assocClassName = assoc.getToClass().getName();
        String assocName = assoc.getAssocName();

        String genericString = "";
        if (modelBaseClasses.containsKey(assocClassName))
            genericString = "<?>";

        // @formatter:off
        String method = "set" + CamelCase.toCamelCase(assocName, true);
        
        String visibility = "public ";
        if (isInternal) {
            method += "Internal";
            visibility = "";
        }
        
        String parameterType = assocClassName + "BaseWrapper" + genericString;
        String parameterName = assocName;
        String property = getPeerProperty(mc, assoc);
        String inverse = isInternal ? "" : createWrappedPropertySetterInverse(mc, assoc);
        
        String result = "    "+visibility+"void "+method+"("+parameterType+" "+parameterName+") {\n" +
                        inverse +
        		        "        setWrappedProperty("+property+", "+parameterName+");\n" +
        		        "    }\n\n";
        // @formatter:on

        return result;
    }

    private String createWrappedPropertySetterInverse(ModelClass mc,
        ClassAssociation assoc) throws Exception {
        ClassAssociation inverse = assoc.getInverse();
        if (inverse == null)
            return "";

        String assocClassName = assoc.getToClass().getName();
        String assocName = assoc.getAssocName();

        String genericString = "";
        if (modelBaseClasses.containsKey(assocClassName))
            genericString = "<?>";

        String property = getPeerProperty(mc, assoc);
        String oldVar = "old" + CamelCase.toCamelCase(assocName, true);
        String paramName = assocName;
        String paramType = assocClassName + "BaseWrapper" + genericString;
        String getter = "get" + CamelCase.toCamelCase(assocName, true);

        String oldMethod = null;
        String method = null;
        if (inverse.getAssociationType() == ClassAssociationType.ONE_TO_ONE
            || inverse.getAssociationType() == ClassAssociationType.ZERO_TO_ONE) {
            oldMethod = "set"
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal(null)";
            method = "set"
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal(this)";
        } else {
            oldMethod = "removeFrom"
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal(Arrays.asList(this))";
            method = "addTo"
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal(Arrays.asList(this))";
            imports.add(Arrays.class.getName());
        }

        // @formatter:off
        String result = "        if (isInitialized("+property+")) {\n" +
                        "            "+paramType+" "+oldVar+" = "+getter+"();\n" +
                        "            if ("+oldVar+" != null) "+oldVar+"."+oldMethod+";\n" +
                        "        }\n" +
                        "        if ("+paramName+" != null) "+paramName+"."+method+";\n"; 
        // @formatter:on

        return result;
    }

    private String createWrappedPropertyGetterInverse(ModelClass mc,
        ClassAssociation assoc) throws Exception {
        ClassAssociation inverse = assoc.getInverse();
        if (inverse == null)
            return "";

        String assocClassName = assoc.getToClass().getName();
        String assocName = assoc.getAssocName();

        String genericString = "";
        if (modelBaseClasses.containsKey(assocClassName))
            genericString = "<?>";

        String paramName = assocName;
        String paramType = assocClassName + "BaseWrapper" + genericString;

        String method = null;
        if (inverse.getAssociationType() == ClassAssociationType.ONE_TO_ONE
            || inverse.getAssociationType() == ClassAssociationType.ZERO_TO_ONE) {
            method = "set"
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal(this)";
        } else {

            method = "addTo"
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal(Arrays.asList(this))";
            imports.add(Arrays.class.getName());
        }

        // @formatter:off
        String castedParamName = "(("+paramType+") "+paramName+")";
        String result = "        if ("+paramName+" != null && notCached) "+castedParamName+"."+method+";\n"; 
        // @formatter:on

        return result;
    }

    private static String getPeerProperty(ModelClass mc, ClassAssociation assoc) {
        return mc.getName() + "Peer."
            + CamelCase.toTitleCase(assoc.getAssocName());
    }

    private String createCollectionGetter(ModelClass mc, ClassAssociation assoc)
        throws Exception {
        String assocClassName = assoc.getToClass().getName();
        String assocWrapperName = new StringBuilder(assocClassName).append(
            "Wrapper").toString();

        if (!wrapperMap.containsKey(assocWrapperName))
            return "";

        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_TO_MANY)
            && (assocType != ClassAssociationType.ONE_TO_MANY)) {
            throw new Exception("class " + mc.getName() + " does not have a "
                + "zero to many or one to many relationship with class"
                + assoc.getClass());
        }

        String assocName = assoc.getAssocName();
        StringBuilder result = new StringBuilder();

        String inverse = createCollectionGetterInverse(mc, assoc);

        // fix warnings
        if (mc.getName().equals("ShippingMethod")) {
            result
                .append("    @SuppressWarnings({ \"unchecked\", \"rawtypes\" })\n");
        }

        String peerProperty = mc.getName() + "Peer."
            + CamelCase.toTitleCase(assocName);

        String notCached = "";
        if (!inverse.isEmpty()) {
            notCached = "        boolean notCached = !isPropertyCached("
                + peerProperty + ");\n";
        }

        result.append("    public List<").append(assocClassName)
            .append("Wrapper> get")
            .append(CamelCase.toCamelCase(assocName, true))
            .append("(boolean sort) {\n").append(notCached)
            .append("        List<").append(assocClassName).append("Wrapper> ")
            .append(assocName).append(" = getWrapperCollection(")
            .append(mc.getName()).append("Peer.")
            .append(CamelCase.toTitleCase(assocName)).append(", ")
            .append(assocClassName).append("Wrapper.class, sort);\n")
            .append(inverse).append("        return ").append(assocName)
            .append(";\n").append("    }\n\n");
        return result.toString();
    }

    private Object createCollectionAdder(ModelClass mc, ClassAssociation assoc,
        boolean isInternal) throws Exception {
        String assocClassName = assoc.getToClass().getName();
        String assocWrapperName = new StringBuilder(assocClassName).append(
            "Wrapper").toString();

        if (!wrapperMap.containsKey(assocWrapperName))
            return "";

        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_TO_MANY)
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
                .append("    @SuppressWarnings({ \"unchecked\", \"rawtypes\" })\n");
        }

        String visibility = "public ";
        String methodName = "addTo" + CamelCase.toCamelCase(assocName, true);
        String inverse = createCollectionSetterInverse(mc, assoc, true);

        if (isInternal) {
            inverse = "";
            visibility = "";
            methodName += "Internal";
        }

        // @formatter:off
        String action = "        addToWrapperCollection(" + mc.getName() + "Peer." + CamelCase.toTitleCase(assocName) + ", " + assocName + ");\n";
        if (isInternal) {
            // use isInitialized instead of isPropertyCached
            // For example, if a new ProcessingEvent is created and a new Specimen
            // as well, then the Specimen.setProcessingEvent() is called with the
            // new ProcessingEvent, then the ProcessingEvent's specimenCollection
            // will be null because it is new. If it's null then it is initialized,
            // so we should be able to immediately add the Specimen to it (because
            // it is new). If we used isPropertyCached, then the
            // ProcessingEventWrapper's specimenCollection property would not be
            // cached and we would not immediately add to it. This is a particular
            // problem when accessing the model underneath directly. For example, if
            // isPropertyCached() returns false, but the model's property is
            // accessed (via property.get()) then a null value can be returned
            // instead of accessing the database.
            
            action = "        if (isInitialized(" + mc.getName() + "Peer." + CamelCase.toTitleCase(assocName) + ")) {\n" +
            		"    " + action +
            		"        } else {\n" +
            		"            getElementQueue().add(" + mc.getName() + "Peer." + CamelCase.toTitleCase(assocName) + ", " + assocName + ");\n" +
            		"        }\n";
        }          
        // @formatter:on

        result.append("    ").append(visibility).append("void ")
            .append(methodName).append("(List<? extends ")
            .append(assocClassName).append("BaseWrapper> ").append(assocName)
            .append(") {\n").append(action).append(inverse).append("    }\n\n");
        return result.toString();
    }

    private Object createCollectionRemover(ModelClass mc,
        ClassAssociation assoc, boolean isInternal) throws Exception {
        String assocClassName = assoc.getToClass().getName();
        String assocWrapperName = new StringBuilder(assocClassName).append(
            "Wrapper").toString();

        if (!wrapperMap.containsKey(assocWrapperName))
            return "";

        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_TO_MANY)
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

        String visibility = "public ";
        String methodName = "removeFrom"
            + CamelCase.toCamelCase(assocName, true);
        String inverse = createCollectionSetterInverse(mc, assoc, false);

        if (isInternal) {
            inverse = "";
            visibility = "";
            methodName += "Internal";
        }

        // @formatter:off
        String action = "        removeFromWrapperCollection(" + mc.getName() + "Peer." + CamelCase.toTitleCase(assocName) + ", " + assocName + ");\n";
        if (isInternal) {
            action = "        if (isPropertyCached(" + mc.getName() + "Peer." + CamelCase.toTitleCase(assocName) + ")) {\n" +
                    "    " + action +
                    "        } else {\n" +
                    "            getElementQueue().remove(" + mc.getName() + "Peer." + CamelCase.toTitleCase(assocName) + ", " + assocName + ");\n" +
                    "        }\n";
        }          
        // @formatter:on

        result.append("    ").append(visibility).append("void ")
            .append(methodName).append("(List<? extends ")
            .append(assocClassName).append("BaseWrapper> ").append(assocName)
            .append(") {\n").append(action).append(inverse).append("    }\n\n");
        return result.toString();
    }

    private Object createCollectionRemoverWithCheck(ModelClass mc,
        ClassAssociation assoc, boolean isInternal) throws Exception {
        String assocClassName = assoc.getToClass().getName();
        String assocWrapperName = new StringBuilder(assocClassName).append(
            "Wrapper").toString();

        if (!wrapperMap.containsKey(assocWrapperName))
            return "";

        ClassAssociationType assocType = assoc.getAssociationType();

        if ((assocType != ClassAssociationType.ZERO_TO_MANY)
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
                .append("    @SuppressWarnings({ \"unchecked\", \"rawtypes\" })\n");
        }

        String visibility = "public ";
        String methodName = "removeFrom"
            + CamelCase.toCamelCase(assocName, true) + "WithCheck";
        String inverse = createCollectionSetterInverse(mc, assoc, false);

        if (isInternal) {
            inverse = "";
            visibility = "";
            methodName += "Internal";
        }

        result.append("    ").append(visibility).append("void ")
            .append(methodName).append("(List<? extends ")
            .append(assocClassName).append("BaseWrapper> ").append(assocName)
            .append(") throws BiobankCheckException {\n")
            .append("        removeFromWrapperCollectionWithCheck(")
            .append(mc.getName()).append("Peer.")
            .append(CamelCase.toTitleCase(assocName)).append(", ")
            .append(assocName).append(");\n").append(inverse)
            .append("    }\n\n");
        return result.toString();
    }

    private String createCollectionSetterInverse(ModelClass mc,
        ClassAssociation assoc, boolean isAdd) throws Exception {
        ClassAssociation inverse = assoc.getInverse();
        if (inverse == null)
            return "";

        String assocClassName = assoc.getToClass().getName();
        String assocName = assoc.getAssocName();

        String paramName = assocName;
        String elementBaseType = assocClassName + "BaseWrapper";

        String method = null;
        if (inverse.getAssociationType() == ClassAssociationType.ONE_TO_ONE
            || inverse.getAssociationType() == ClassAssociationType.ZERO_TO_ONE) {
            method = "set"
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal" + (isAdd ? "(this)" : "(null)");
        } else {
            method = (isAdd ? "addTo" : "removeFrom")
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal(Arrays.asList(this))";
            imports.add(Arrays.class.getName());
        }

        // @formatter:off
        String result = "        for ("+elementBaseType+" e : "+paramName+") {\n" +
                        "            e."+method+";\n" +
                        "        }\n";
        // @formatter:on

        return result;
    }

    private String createCollectionGetterInverse(ModelClass mc,
        ClassAssociation assoc) throws Exception {
        ClassAssociation inverse = assoc.getInverse();
        if (inverse == null)
            return "";

        String assocClassName = assoc.getToClass().getName();
        String assocName = assoc.getAssocName();

        String paramName = assocName;
        String elementBaseType = assocClassName + "BaseWrapper";

        String method = null;
        if (inverse.getAssociationType() == ClassAssociationType.ONE_TO_ONE
            || inverse.getAssociationType() == ClassAssociationType.ZERO_TO_ONE) {
            method = "set"
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal(this)";
        } else {
            method = "addTo"
                + CamelCase.toCamelCase(inverse.getAssocName(), true)
                + "Internal(Arrays.asList(this))";
            imports.add(Arrays.class.getName());
        }

        // @formatter:off
        String result = "        if (notCached) {\n" +
                        "            for ("+elementBaseType+" e : "+paramName+") {\n" +
                        "                e."+method+";\n" +
                        "            }\n" +
                        "        }\n";
        // @formatter:on

        return result;
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
            if (((assocType == ClassAssociationType.ZERO_TO_MANY) || (assocType == ClassAssociationType.ONE_TO_MANY))
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
            StringBuilder baseWrapperName = new StringBuilder(toClass.getName())
                .append("BaseWrapper");

            String packagename = wrapperMap.get(wrapperName.toString());

            if (packagename == null) {
                // this wrapper does not exist
                continue;
            }

            importCount.put(toClass.getName(), 1);

            sb.append("import ").append(packagename).append(".")
                .append(wrapperName).append(";\n");

            sb.append("import edu.ualberta.med.biobank.common.wrappers.base.")
                .append(baseWrapperName).append(";\n");
        }

        return sb.toString();
    }

}
