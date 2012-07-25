package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "REPORT_COLUMN")
public class ReportColumn extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer position;
    private PropertyModifier propertyModifier;
    private EntityColumn entityColumn;

    @Column(name = "POSITION")
    public Integer getPosition() {
        return this.position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROPERTY_MODIFIER_ID")
    public PropertyModifier getPropertyModifier() {
        return this.propertyModifier;
    }

    public void setPropertyModifier(PropertyModifier propertyModifier) {
        this.propertyModifier = propertyModifier;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COLUMN_ID", nullable = false)
    public EntityColumn getEntityColumn() {
        return this.entityColumn;
    }

    public void setEntityColumn(EntityColumn entityColumn) {
        this.entityColumn = entityColumn;
    }
}
