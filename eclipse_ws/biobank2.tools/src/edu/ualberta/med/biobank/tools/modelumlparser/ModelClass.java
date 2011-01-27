package edu.ualberta.med.biobank.tools.modelumlparser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelClass {

    private String name;

    private String pkg;

    private ModelClass extendsClass;

    private Map<String, String> attrMap;

    private Map<String, ClassAssociation> assocMap;

    public ModelClass(String name) {
        this.name = name;
        extendsClass = null;
        attrMap = new HashMap<String, String>();
        assocMap = new HashMap<String, ClassAssociation>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public ModelClass getExtendsClass() {
        return extendsClass;
    }

    public void setExtendsClass(ModelClass extendsClass) {
        this.extendsClass = extendsClass;
    }

    public Map<String, String> getAttrMap() {
        return Collections.unmodifiableMap(attrMap);
    }

    public void addAttr(String attrName, String attrType) {
        attrMap.put(attrName, attrType);
    }

    public Map<String, ClassAssociation> getAssocMap() {
        return Collections.unmodifiableMap(assocMap);
    }

    public void addAssoc(String assocName, ClassAssociation assoc) {
        assocMap.put(assocName, assoc);
    }

}
