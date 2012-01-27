package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class EntityProperty extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String property;
    private PropertyType propertyType;
    private Collection<EntityColumn> entityColumnCollection =
        new HashSet<EntityColumn>();
    private Collection<EntityFilter> entityFilterCollection =
        new HashSet<EntityFilter>();

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Collection<EntityColumn> getEntityColumnCollection() {
        return entityColumnCollection;
    }

    public void setEntityColumnCollection(
        Collection<EntityColumn> entityColumnCollection) {
        this.entityColumnCollection = entityColumnCollection;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public Collection<EntityFilter> getEntityFilterCollection() {
        return entityFilterCollection;
    }

    public void setEntityFilterCollection(
        Collection<EntityFilter> entityFilterCollection) {
        this.entityFilterCollection = entityFilterCollection;
    }
}
