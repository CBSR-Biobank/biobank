package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@javax.persistence.Entity
@Table(name = "ENTITY")
public class Entity extends AbstractModel
    implements HasName {
    private static final long serialVersionUID = 1L;

    private String className;
    private String name;
    private Set<Report> reports = new HashSet<Report>(0);
    private Set<EntityProperty> entityProperties = new HashSet<EntityProperty>(
        0);

    @NotEmpty(message = "{Entity.className.NotEmpty}")
    @Column(name = "CLASS_NAME")
    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    @NotEmpty(message = "{Entity.name.NotEmpty}")
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Set<Report> getReports() {
        return this.reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Set<EntityProperty> getEntityProperties() {
        return this.entityProperties;
    }

    public void setEntityProperties(Set<EntityProperty> entityProperties) {
        this.entityProperties = entityProperties;
    }
}
