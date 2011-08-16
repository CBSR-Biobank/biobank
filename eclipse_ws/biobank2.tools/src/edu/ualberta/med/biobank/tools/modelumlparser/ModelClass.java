package edu.ualberta.med.biobank.tools.modelumlparser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelClass {

    private String name;

    private String pkg;

    private ModelClass extendsClass;

    private Map<String, Attribute> attrMap;

    private Map<String, ClassAssociation> assocMap;

    private boolean isParentClass;

    public ModelClass(String name) {
        this.name = name;
        extendsClass = null;
        attrMap = new HashMap<String, Attribute>();
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

    public Map<String, Attribute> getAttrMap() {
        return Collections.unmodifiableMap(attrMap);
    }

    public void addAttr(String attrName, Attribute attr) {
        attrMap.put(attrName, attr);
    }

    public Map<String, ClassAssociation> getAssocMap() {
        return Collections.unmodifiableMap(assocMap);
    }

    public void addAssoc(String assocName, ClassAssociation assoc) {
        assocMap.put(assocName, assoc);
    }

    public void setIsParentClass(boolean isParentClass) {
        this.isParentClass = isParentClass;
    }

    public boolean getIsParentClass() {
        return isParentClass;
    }

}
