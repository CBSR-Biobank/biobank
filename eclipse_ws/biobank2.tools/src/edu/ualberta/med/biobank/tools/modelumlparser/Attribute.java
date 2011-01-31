package edu.ualberta.med.biobank.tools.modelumlparser;

import java.util.HashSet;
import java.util.Set;

public class Attribute {
    String name;
    String type;
    Set<String> stereotypes;

    public Attribute(String name, String type) {
        this(name, type, null);
    }

    public Attribute(String name, String type, Set<String> stereotypes) {
        super();
        this.name = name;
        this.type = type;
        setStereotypes(stereotypes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean hasStereotype(String stereotype) {
        return stereotypes.contains(stereotype);
    }

    public void addStereotype(String stereotype) {
        stereotypes.add(stereotype);
    }

    public void setStereotypes(Set<String> stereotypes) {
        if (stereotypes == null)
            this.stereotypes = new HashSet<String>();
        else
            this.stereotypes = stereotypes;
    }

    public Set<String> getStereotypes() {
        return stereotypes;
    }

}
