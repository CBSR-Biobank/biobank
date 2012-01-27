package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class Entity extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String className;
    private String name;
    private Collection<Report> reportCollection = new HashSet<Report>();
    private Collection<EntityProperty> entityPropertyCollection =
        new HashSet<EntityProperty>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Report> getReportCollection() {
        return reportCollection;
    }

    public void setReportCollection(Collection<Report> reportCollection) {
        this.reportCollection = reportCollection;
    }

    public Collection<EntityProperty> getEntityPropertyCollection() {
        return entityPropertyCollection;
    }

    public void setEntityPropertyCollection(
        Collection<EntityProperty> entityPropertyCollection) {
        this.entityPropertyCollection = entityPropertyCollection;
    }
}
