package edu.ualberta.med.biobank.action.reports;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.exception.ActionException;

public class AdvancedReportSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    // private AdvancedReportSaveInfo info;

    // public AdvancedReportSaveAction(AdvancedReportSaveInfo info) {
    // this.info = info;
    // }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return false;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        /*
         * Report report = context.load(Report.class, info.id, new Report());
         * 
         * report.setName(info.name); report.setDescription(info.description);
         * report.setIsCount(info.isCount); report.setIsPublic(info.isPublic);
         * 
         * report.setEntity(info.entity); report.setUserId(info.userId);
         * 
         * Set<ReportColumn> reportColumns = new HashSet<ReportColumn>(); for
         * (ReportColumn column : notNull(info.reportColumns)) { ReportColumn
         * columnCopy = new ReportColumn();
         * columnCopy.setEntityColumn(column.getEntityColumn());
         * columnCopy.setPosition(column.getPosition());
         * columnCopy.setPropertyModifier(column.getPropertyModifier());
         * 
         * reportColumns.add(columnCopy); }
         * report.setReportColumns(reportColumns);
         * 
         * Set<ReportFilter> reportFilters = new HashSet<ReportFilter>(); for
         * (ReportFilter filter : notNull(info.reportFilters) { ReportFilter
         * filterCopy = new ReportFilter();
         * filterCopy.setEntityFilter(filter.getEntityFilter());
         * filterCopy.setOperator(filter.getOperator());
         * filterCopy.setPosition(filter.getPosition());
         * 
         * Set<ReportFilterValue> values = new HashSet<ReportFilterValue>(); for
         * (ReportFilterValue value : notNull(filter .getReportFilterValues()))
         * { ReportFilterValue valueCopy = new ReportFilterValue();
         * valueCopy.setPosition(value.getPosition());
         * valueCopy.setValue(value.getValue());
         * valueCopy.setSecondValue(value.getSecondValue());
         * 
         * values.add(valueCopy); } filterCopy.setReportFilterValues(values);
         * 
         * reportFilters.add(filterCopy); }
         * report.setReportFilters(reportFilters);
         */
        return new IdResult(2);
    }
}
