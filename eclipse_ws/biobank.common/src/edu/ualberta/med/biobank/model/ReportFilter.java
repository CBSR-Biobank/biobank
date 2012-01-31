package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "REPORT_FILTER")
public class ReportFilter extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer position;
    private Integer operator;
    private Set<ReportFilterValue> reportFilterValueCollection =
        new HashSet<ReportFilterValue>(0);
    private EntityFilter entityFilter;

    @Column(name = "POSITION")
    public Integer getPosition() {
        return this.position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Column(name = "OPERATOR")
    public Integer getOperator() {
        return this.operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "REPORT_FILTER_ID", updatable = false)
    public Set<ReportFilterValue> getReportFilterValueCollection() {
        return this.reportFilterValueCollection;
    }

    public void setReportFilterValueCollection(
        Set<ReportFilterValue> reportFilterValueCollection) {
        this.reportFilterValueCollection = reportFilterValueCollection;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_FILTER_ID", nullable = false)
    public EntityFilter getEntityFilter() {
        return this.entityFilter;
    }

    public void setEntityFilter(EntityFilter entityFilter) {
        this.entityFilter = entityFilter;
    }
}
