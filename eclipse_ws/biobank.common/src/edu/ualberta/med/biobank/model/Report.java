package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class Report extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Integer userId;
    private Boolean isPublic = false;
    private Boolean isCount = false;
    private Collection<ReportColumn> reportColumnCollection =
        new HashSet<ReportColumn>();
    private Entity entity;
    private Collection<ReportFilter> reportFilterCollection =
        new HashSet<ReportFilter>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsCount() {
        return isCount;
    }

    public void setIsCount(Boolean isCount) {
        this.isCount = isCount;
    }

    public Collection<ReportColumn> getReportColumnCollection() {
        return reportColumnCollection;
    }

    public void setReportColumnCollection(
        Collection<ReportColumn> reportColumnCollection) {
        this.reportColumnCollection = reportColumnCollection;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Collection<ReportFilter> getReportFilterCollection() {
        return reportFilterCollection;
    }

    public void setReportFilterCollection(
        Collection<ReportFilter> reportFilterCollection) {
        this.reportFilterCollection = reportFilterCollection;
    }
}
