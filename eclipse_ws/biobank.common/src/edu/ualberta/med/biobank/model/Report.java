package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@javax.persistence.Entity
@Table(name = "REPORT")
public class Report extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Integer userId;
    private boolean isPublic;
    private boolean isCount;
    private Collection<ReportColumn> reportColumnCollection =
        new HashSet<ReportColumn>(0);
    private Entity entity;
    private Collection<ReportFilter> reportFilterCollection =
        new HashSet<ReportFilter>(0);

    @NotEmpty
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION", columnDefinition="TEXT")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "USER_ID")
    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "IS_PUBLIC")
    // TODO: rename to isPublic
    public boolean getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Column(name = "IS_COUNT")
    // TODO: rename to isCount
    public boolean getIsCount() {
        return this.isCount;
    }

    public void setIsCount(boolean isCount) {
        this.isCount = isCount;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID", updatable = false)
    public Collection<ReportColumn> getReportColumnCollection() {
        return this.reportColumnCollection;
    }

    public void setReportColumnCollection(
        Collection<ReportColumn> reportColumnCollection) {
        this.reportColumnCollection = reportColumnCollection;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_ID", nullable = false)
    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID", updatable = false)
    public Collection<ReportFilter> getReportFilterCollection() {
        return this.reportFilterCollection;
    }

    public void setReportFilterCollection(
        Collection<ReportFilter> reportFilterCollection) {
        this.reportFilterCollection = reportFilterCollection;
    }
}
