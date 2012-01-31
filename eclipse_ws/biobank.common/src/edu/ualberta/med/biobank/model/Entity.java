package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;

@javax.persistence.Entity
@Table(name = "ENTITY")
public class Entity extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String className;
    private String name;
    private Set<Report> reportCollection = new HashSet<Report>(0);
    private Set<EntityProperty> entityPropertyCollection =
        new HashSet<EntityProperty>(0);

    @NotEmpty
    @Column(name = "CLASS_NAME")
    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @NotEmpty
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Set<Report> getReportCollection() {
        return this.reportCollection;
    }

    public void setReportCollection(Set<Report> reportCollection) {
        this.reportCollection = reportCollection;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Set<EntityProperty> getEntityPropertyCollection() {
        return this.entityPropertyCollection;
    }

    public void setEntityPropertyCollection(
        Set<EntityProperty> entityPropertyCollection) {
        this.entityPropertyCollection = entityPropertyCollection;
    }
}
