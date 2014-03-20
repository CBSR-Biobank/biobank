package edu.ualberta.med.biobank.treeview.report;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportsGetAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PrivateReportsGroup extends AbstractReportGroup {
    private static final I18n i18n = I18nFactory.getI18n(PrivateReportsGroup.class);

    private static Logger log = LoggerFactory.getLogger(PrivateReportsGroup.class.getName());

    @SuppressWarnings("nls")
    private static final String NODE_NAME = i18n.tr("My Reports");

    public PrivateReportsGroup(AdapterBase parent, int id) {
        super(parent, id, NODE_NAME);
    }

    @SuppressWarnings("nls")
    @Override
    protected Collection<ReportWrapper> getReports() throws ApplicationException {
        if (!SessionManager.getInstance().isConnected()) {
            throw new IllegalStateException("should only be called when user is logged in");
        }

        User user = SessionManager.getUser().getWrappedObject();
        List<Report> rawReports = SessionManager.getAppService()
            .doAction(new AdvancedReportsGetAction(user)).getList();
        List<ReportWrapper> reports = ModelWrapper.wrapModelCollection(
            SessionManager.getAppService(), rawReports, ReportWrapper.class);

        if (!reports.isEmpty()) {
            log.debug("reports size: {}", reports.size());
        }

        return reports;
    }

}
