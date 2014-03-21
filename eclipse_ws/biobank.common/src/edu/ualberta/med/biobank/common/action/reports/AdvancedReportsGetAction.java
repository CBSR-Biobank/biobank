package edu.ualberta.med.biobank.common.action.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportsGetAction.AdvancedReportsData;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.User;

/**
 * Returns the reports belonging to a user. Both the user's private reports and those shared by the
 * groups he/she is in are returned.
 * 
 * @author loyola
 * 
 */
public class AdvancedReportsGetAction implements Action<AdvancedReportsData> {
    private static final long serialVersionUID = 1L;

    private final Integer userId;

    @SuppressWarnings("nls")
    public AdvancedReportsGetAction(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user is null");
        }
        this.userId = user.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ReportsPermission().isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public AdvancedReportsData run(ActionContext context) throws ActionException {
        if (userId == null) {
            throw new IllegalStateException("user id is null");
        }

        List<Report> userReports = getUserReports(context);
        List<Report> sharedReports = getSharedReports(context);

        return new AdvancedReportsData(userReports, sharedReports);
    }

    @SuppressWarnings({ "nls", "unchecked" })
    private List<Report> getUserReports(ActionContext context) {
        List<Report> reports = context.getSession().createCriteria(Report.class, "report")
            .createAlias("report.user", "user")
            .add(Restrictions.eq("user.id", userId))
            .add(Restrictions.eq("isPublic", false))
            .list();

        return reports;
    }

    @SuppressWarnings({ "unchecked", "nls" })
    private List<Report> getSharedReports(ActionContext context) {
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
            if (userIdsInGroup.contains(report.getUser().getId())) {
                results.add(report);
            }
        }

        return results;
    }

    public static class AdvancedReportsData implements ActionResult {
        private static final long serialVersionUID = 1L;

        public final List<Report> userReports;
        public final List<Report> sharedReports;

        public AdvancedReportsData(List<Report> userReports, List<Report> sharedReports) {
            this.userReports = Collections.unmodifiableList(userReports);
            this.sharedReports = Collections.unmodifiableList(sharedReports);
        }
    }

}
