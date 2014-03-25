package edu.ualberta.med.biobank.common.action.reports;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportGetAction.ReportData;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityProperty;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class AdvancedReportGetAction implements Action<ReportData> {
    private static final long serialVersionUID = 1L;

    private final Integer reportId;

    @SuppressWarnings("nls")
    public AdvancedReportGetAction(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("report is null");
        }
        this.reportId = report.getId();
    }

    public AdvancedReportGetAction(Integer reportId) {
        this.reportId = reportId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ReportsPermission().isAllowed(context);
    }

    @Override
    public ReportData run(ActionContext context) throws ActionException {
        Report report = context.load(Report.class, reportId);

        // load associations
        report.getUser().getLogin();
        for (ReportColumn reportColumn : report.getReportColumns()) {
            reportColumn.getPosition();
        }
        for (ReportFilter reportFilter : report.getReportFilters()) {
            for (ReportFilterValue filterValue : reportFilter.getReportFilterValues()) {
                filterValue.getValue();
            }
        }
        Entity entity = report.getEntity();
        if (entity != null) {
            for (EntityProperty entityProperty : entity.getEntityProperties()) {
                for (EntityColumn entityColumn : entityProperty.getEntityColumns()) {
                    entityColumn.getName();
                }
            }
        }
        return new ReportData(report);
    }

    public static class ReportData implements ActionResult {
        private static final long serialVersionUID = 1L;

        public final Report report;

        public ReportData(Report report) {
            this.report = report;
        }

        public ReportData() {
            this.report = null;
        }

    }

}
