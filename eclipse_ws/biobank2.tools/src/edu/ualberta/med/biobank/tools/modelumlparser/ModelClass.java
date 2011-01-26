package edu.ualberta.med.biobank.tools.modelumlparser;

import java.util.HashMap;
import java.util.Map;

public class ModelClass {
    private String name;

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

    public ModelClass getExtendsClass() {
        return extendsClass;
    }

    public void setExtendsClass(ModelClass extendsClass) {
        this.extendsClass = extendsClass;
    }

    public Map<String, String> getAttrMap() {
        return attrMap;
    }

    public void setAttrMap(Map<String, String> attrMap) {
        this.attrMap = attrMap;
    }

    public Map<String, ClassAssociation> getAssocMap() {
        return assocMap;
    }

    public void setAssocMap(Map<String, ClassAssociation> assocMap) {
        this.assocMap = assocMap;
    }

}
