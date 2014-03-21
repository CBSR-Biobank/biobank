package edu.ualberta.med.biobank.treeview.report;

import java.util.Collection;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PrivateReportsGroup extends AbstractReportGroup {
    private static final I18n i18n = I18nFactory.getI18n(PrivateReportsGroup.class);

    @SuppressWarnings("nls")
    private static final String NODE_NAME = i18n.tr("My Reports");

    private final List<ReportWrapper> reports;

    public PrivateReportsGroup(AdapterBase parent, int id, List<ReportWrapper> userReports) {
        super(parent, id, NODE_NAME);
        this.reports = userReports;
    }

    @Override
    protected Collection<ReportWrapper> getReports() throws ApplicationException {
        return reports;
    }

}
