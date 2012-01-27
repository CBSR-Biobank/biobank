package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "ENTITY_COLUMN")
public class EntityColumn extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private EntityProperty entityProperty;

    @NotEmpty
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_PROPERTY_ID", nullable = false)
    public EntityProperty getEntityProperty() {
        return this.entityProperty;
    }

    public void setEntityProperty(EntityProperty entityProperty) {
        this.entityProperty = entityProperty;
    }
}
