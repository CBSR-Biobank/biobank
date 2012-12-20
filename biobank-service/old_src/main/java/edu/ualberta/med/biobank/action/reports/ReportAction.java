package edu.ualberta.med.biobank.action.reports;

import java.lang.reflect.Constructor;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ProxiedListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.reports.BiobankReport;

public class ReportAction implements Action<ProxiedListResult<Object>> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString UNKNOWN_PROBLEM_ERRMSG =
        bundle.tr("Unable to run report for unknown reason").format();

    BiobankReport report;

    public ReportAction(BiobankReport report) {
        this.report = report;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ReportsPermission().isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public ProxiedListResult<Object> run(ActionContext context)
        throws ActionException {
        try {
            Class<?> cls =
                Class.forName("edu.ualberta.med.biobank.reports."
                    + report.getClassName());
            Class<?> partypes[] = new Class[] { BiobankReport.class };
            Constructor<?> constructor = cls.getConstructor(partypes);
            AbstractReport runReport =
                (AbstractReport) constructor.newInstance(report);
            return new ProxiedListResult<Object>(
                runReport.generate(context
                    .getAppService()));
        } catch (Exception e) {
            throw new LocalizedException(UNKNOWN_PROBLEM_ERRMSG, e);
        }
    }
}
