package edu.ualberta.med.biobank.model.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.model.LongIdModel;

@Entity
@Table(name = "ENTITY_FILTER")
public class EntityFilter extends LongIdModel {
    private static final long serialVersionUID = 1L;

    private Integer filterType;
    private String name;
    private EntityProperty entityProperty;

    @Column(name = "FILTER_TYPE")
    public Integer getFilterType() {
        return this.filterType;
    }

    public void setFilterType(Integer filterType) {
        this.filterType = filterType;
    }

    @NotEmpty(message = "{EntityFilter.name.NotEmpty}")
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull(message = "{EntityFilter.entityProperty.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITY_PROPERTY_ID", nullable = false)
    public EntityProperty getEntityProperty() {
        return this.entityProperty;
    }

    public void setEntityProperty(EntityProperty entityProperty) {
        this.entityProperty = entityProperty;
    }
}
