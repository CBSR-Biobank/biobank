package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "ENTITY_PROPERTY")
public class EntityProperty extends LongIdModel {
    private static final long serialVersionUID = 1L;

    private String property;
    private Set<EntityColumn> entityColumns = new HashSet<EntityColumn>(0);
    private PropertyType propertyType;
    private Set<EntityFilter> entityFilters = new HashSet<EntityFilter>(0);

    @NotEmpty(message = "{EntityProperty.property.NotEmpty}")
    @Column(name = "PROPERTY")
    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_PROPERTY_ID", updatable = false)
    public Set<EntityColumn> getEntityColumns() {
        return this.entityColumns;
    }

    public void setEntityColumns(Set<EntityColumn> entityColumns) {
        this.entityColumns = entityColumns;
    }

    @NotNull(message = "{EntityProperty.propertyType.NotNull}")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROPERTY_TYPE_ID", nullable = false)
    public PropertyType getPropertyType() {
        return this.propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_PROPERTY_ID", updatable = false)
    public Set<EntityFilter> getEntityFilters() {
        return this.entityFilters;
    }

    public void setEntityFilters(Set<EntityFilter> entityFilters) {
        this.entityFilters = entityFilters;
    }
}
