package edu.ualberta.med.biobank.treeview.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PrivateReportsGroup extends AbstractReportGroup {
    private static final String NODE_NAME = Messages.PrivateReportsGroup_myreport_node_label;
    private static final String HQL_REPORT_OF_USER = "from " //$NON-NLS-1$
        + Report.class.getName() + " where userId = ?"; //$NON-NLS-1$

    public PrivateReportsGroup(AdapterBase parent, int id) {
        super(parent, id, NODE_NAME);
    }

    @Override
    protected Collection<ReportWrapper> getReports() {
        List<ReportWrapper> reports = new ArrayList<ReportWrapper>();

        if (SessionManager.getInstance().isConnected()) {
            Integer userId = SessionManager.getUser().getId().intValue();
            HQLCriteria criteria = new HQLCriteria(HQL_REPORT_OF_USER,
                Arrays.asList(new Object[] { userId }));
            try {
                List<Report> rawReports = SessionManager.getAppService().query(
                    criteria);
                for (Report rawReport : rawReports) {
                    reports.add(new ReportWrapper(SessionManager
                        .getAppService(), rawReport));
                }
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
        }

        return reports;
    }
}
