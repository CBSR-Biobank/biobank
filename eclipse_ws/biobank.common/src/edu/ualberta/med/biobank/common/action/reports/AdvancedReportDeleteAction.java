package edu.ualberta.med.biobank.common.action.reports;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.model.Report;

public class AdvancedReportDeleteAction implements Action<EmptyResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Integer reportId;

    public AdvancedReportDeleteAction(Integer reportId) {
        this.reportId = reportId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ReportsPermission().isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        context.getSession().delete(context.load(Report.class, reportId));
        return new EmptyResult();
    }

}
