package edu.ualberta.med.biobank.common.action.reports;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityProperty;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.User;

/**
 * Returns the advanced reports that are shared by the group the user belongs to.
 * 
 * @author loyola
 * 
 */
public class AdvancedReportGetSharedAction implements Action<ListResult<Report>> {
    private static final long serialVersionUID = 1L;

    private final Integer userId;

    @SuppressWarnings("nls")
    public AdvancedReportGetSharedAction(User user) {
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
        User user = context.load(User.class, userId);
        Set<Integer> userIdsInGroup = new HashSet<Integer>();
        for (Group group : user.getGroups()) {
            for (User ug : group.getUsers()) {
                userIdsInGroup.add(ug.getId());
            }
        }

        List<Report> results = new ArrayList<Report>();
        List<Report> reports = context.getSession().createCriteria(Report.class)
            .add(Restrictions.eq("isPublic", true))
            .list();

        for (Report report : reports) {
            if (userIdsInGroup.contains(report.getUserId())) {
                // load associations
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

                results.add(report);
            }
        }

        return new ListResult<Report>(results);
    }
}
