package edu.ualberta.med.biobank.model;

public class EntityColumn extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private EntityProperty entityProperty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityProperty getEntityProperty() {
        return entityProperty;
    }

    public void setEntityProperty(EntityProperty entityProperty) {
        this.entityProperty = entityProperty;
    }
}
