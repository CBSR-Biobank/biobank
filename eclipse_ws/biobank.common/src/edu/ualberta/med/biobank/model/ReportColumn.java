package edu.ualberta.med.biobank.model;

public class ReportColumn extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer position;
    private PropertyModifier propertyModifier;
    private EntityColumn entityColumn;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public PropertyModifier getPropertyModifier() {
        return propertyModifier;
    }

    public void setPropertyModifier(PropertyModifier propertyModifier) {
        this.propertyModifier = propertyModifier;
    }

    public EntityColumn getEntityColumn() {
        return entityColumn;
    }

    public void setEntityColumn(EntityColumn entityColumn) {
        this.entityColumn = entityColumn;
    }
}
