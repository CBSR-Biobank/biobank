package edu.ualberta.med.biobank.model;

public class PropertyModifier extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    public String name;
    public String propertyModifier;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPropertyModifier() {
        return propertyModifier;
    }

    public void setPropertyModifier(String propertyModifier) {
        this.propertyModifier = propertyModifier;
    }
}
