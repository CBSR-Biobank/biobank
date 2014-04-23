package edu.ualberta.med.biobank.common.action.reports;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.GlobalAdminPermission;
import edu.ualberta.med.biobank.model.Report;

public class AdvancedReportDeleteAction implements Action<EmptyResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Integer reportId;

    public AdvancedReportDeleteAction(Integer reportId) {
        this.reportId = reportId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Report report = context.load(Report.class, reportId);

        return (report.getUser().equals(context.getUser())
        || new GlobalAdminPermission().isAllowed(context));
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        context.getSession().delete(context.load(Report.class, reportId));
        return new EmptyResult();
    }

}
