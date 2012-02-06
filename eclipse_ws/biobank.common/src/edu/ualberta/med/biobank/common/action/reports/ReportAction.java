package edu.ualberta.med.biobank.common.action.reports;

import java.lang.reflect.Constructor;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.server.reports.AbstractReport;

public class ReportAction implements Action<ListResult<Object>> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    BiobankReport report;

    public ReportAction(BiobankReport report) {
        this.report = report;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.REPORTS.isAllowed(context.getUser());
    }

    @Override
    public ListResult<Object> run(ActionContext context) throws ActionException {
        try {
            Class<?> cls =
                Class.forName("edu.ualberta.med.biobank.server.reports."
                    + report.getClassName());
            Class<?> partypes[] = new Class[] { BiobankReport.class };
            Constructor<?> constructor = cls.getConstructor(partypes);
            AbstractReport runReport =
                (AbstractReport) constructor.newInstance(report);
            return new ListResult<Object>(runReport.generate(context
                .getAppService()));
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }
}
