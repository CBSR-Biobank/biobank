package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@javax.persistence.Entity
@Table(name = "REPORT")
public class Report extends LongIdModel
    implements HasName, HasDescription {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Report",
        "Reports");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString NAME = bundle.trc(
            "model",
            "Name").format();
    }

    private String name;
    private String description;
    private Integer userId;
    private Boolean isPublic;
    private Boolean isCount;
    private Set<ReportColumn> reportColumns = new HashSet<ReportColumn>(0);
    private Entity entity;
    private Set<ReportFilter> reportFilters = new HashSet<ReportFilter>(0);

    @Override
    @NotEmpty(message = "{Report.name.NotEmpty}")
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    public String getDescription() {
        return this.description;
    }

    @Override
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

    @Column(name = "IS_PUBLIC", nullable = false)
    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Column(name = "IS_COUNT", nullable = false)
    public Boolean getIsCount() {
        return this.isCount;
    }

    public void setIsCount(Boolean isCount) {
        this.isCount = isCount;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    public Set<ReportColumn> getReportColumns() {
        return this.reportColumns;
    }

    public void setReportColumns(Set<ReportColumn> reportColumns) {
        this.reportColumns = reportColumns;
    }

    @NotNull(message = "{Report.entity.NotNull}")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_ID", nullable = false)
    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    public Set<ReportFilter> getReportFilters() {
        return this.reportFilters;
    }

    public void setReportFilters(Set<ReportFilter> reportFilters) {
        this.reportFilters = reportFilters;
    }
}
