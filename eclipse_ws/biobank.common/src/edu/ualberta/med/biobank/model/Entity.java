package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "ENTITY")
public class Entity extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String className;
    private String name;
    private Collection<Report> reportCollection = new HashSet<Report>(0);
    private Collection<EntityProperty> entityPropertyCollection =
        new HashSet<EntityProperty>(0);

    @Column(name = "CLASS_NAME")
    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Collection<Report> getReportCollection() {
        return this.reportCollection;
    }

    public void setReportCollection(Collection<Report> reportCollection) {
        this.reportCollection = reportCollection;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITY_ID", updatable = false)
    public Collection<EntityProperty> getEntityPropertyCollection() {
        return this.entityPropertyCollection;
    }

    public void setEntityPropertyCollection(
        Collection<EntityProperty> entityPropertyCollection) {
        this.entityPropertyCollection = entityPropertyCollection;
    }
}
