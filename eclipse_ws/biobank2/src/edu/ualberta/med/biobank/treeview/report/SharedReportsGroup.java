package edu.ualberta.med.biobank.treeview.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportGetSharedAction;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SharedReportsGroup extends AbstractReportGroup {
    private static final I18n i18n = I18nFactory.getI18n(SharedReportsGroup.class);

    @SuppressWarnings("nls")
    private static final String NODE_NAME = i18n.tr("Shared Reports");

    public SharedReportsGroup(AdapterBase parent, int id) {
        super(parent, id, NODE_NAME);
    }

    @Override
    protected Collection<ReportWrapper> getReports() throws ApplicationException {
        List<ReportWrapper> reports = new ArrayList<ReportWrapper>();

        if (SessionManager.getInstance().isConnected()) {
            try {
                if (!SessionManager.getAppService().isAllowed(new ReportsPermission())) {
                    return reports;
                }
            } catch (ApplicationException e2) {
                return reports;
            }

            User user = SessionManager.getUser().getWrappedObject();

            List<Report> rawReports = SessionManager.getAppService()
                .doAction(new AdvancedReportGetSharedAction(user)).getList();
            reports.addAll(ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(), rawReports, ReportWrapper.class));

        }

        return reports;
    }
}
