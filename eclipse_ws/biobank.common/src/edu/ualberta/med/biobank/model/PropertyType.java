package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class PropertyType extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private Collection<PropertyModifier> propertyModifierCollection =
        new HashSet<PropertyModifier>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<PropertyModifier> getPropertyModifierCollection() {
        return propertyModifierCollection;
    }

    public void setPropertyModifierCollection(
        Collection<PropertyModifier> propertyModifierCollection) {
        this.propertyModifierCollection = propertyModifierCollection;
    }
}
