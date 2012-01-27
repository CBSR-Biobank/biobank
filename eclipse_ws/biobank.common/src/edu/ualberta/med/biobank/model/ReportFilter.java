package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class ReportFilter extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer position;
    private Integer operator;
    private Collection<ReportFilterValue> reportFilterValueCollection =
        new HashSet<ReportFilterValue>();
    private EntityFilter entityFilter;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Collection<ReportFilterValue> getReportFilterValueCollection() {
        return reportFilterValueCollection;
    }

    public void setReportFilterValueCollection(
        Collection<ReportFilterValue> reportFilterValueCollection) {
        this.reportFilterValueCollection = reportFilterValueCollection;
    }

    public EntityFilter getEntityFilter() {
        return entityFilter;
    }

    public void setEntityFilter(EntityFilter entityFilter) {
        this.entityFilter = entityFilter;
    }
}
