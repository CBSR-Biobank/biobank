package edu.ualberta.med.biobank.model;

public class EntityFilter extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer filterType;
    private String name;
    private EntityProperty entityProperty;

    public Integer getFilterType() {
        return filterType;
    }

    public void setFilterType(Integer filterType) {
        this.filterType = filterType;
    }

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
