package edu.ualberta.med.biobank.common.action.reports;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityProperty;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.User;

public class AdvancedReportsGetAction implements Action<ListResult<Report>> {
    private static final long serialVersionUID = 1L;

    private final Integer userId;

    @SuppressWarnings("nls")
    public AdvancedReportsGetAction(User user) {
        if (user == null) {
            throw new IllegalArgumentException("null user");
        }
        this.userId = user.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ReportsPermission().isAllowed(context);
    }

    @SuppressWarnings({ "unchecked", "nls" })
    @Override
    public ListResult<Report> run(ActionContext context) throws ActionException {
        List<Report> reports = context.getSession().createCriteria(Report.class, "report")
            .createAlias("report.user", "user")
            .add(Restrictions.eq("user.id", userId))
            .list();

        for (Report report : reports) {
            AdvancedReportsGetAction.loadAssociations(report);
        }

        return new ListResult<Report>(reports);
    }

    public static void loadAssociations(Report report) {
        for (ReportColumn reportColumn : report.getReportColumns()) {
            reportColumn.getPosition();
        }
        for (ReportFilter reportFilter : report.getReportFilters()) {
            reportFilter.getPosition();
        }
        Entity entity = report.getEntity();
        if (entity != null) {
            for (EntityProperty entityProperty : entity.getEntityProperties()) {
                for (EntityColumn entityColumn : entityProperty.getEntityColumns()) {
                    entityColumn.getName();
                }
            }
        }

    }

}
