package edu.ualberta.med.biobank.treeview.report;

import java.util.Collection;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SharedReportsGroup extends AbstractReportGroup {
    private static final I18n i18n = I18nFactory.getI18n(SharedReportsGroup.class);

    @SuppressWarnings("nls")
    private static final String NODE_NAME = i18n.tr("Shared Reports");

    private final int NODE_ID = 200;

    private final List<ReportWrapper> reports;

    public SharedReportsGroup(AdapterBase parent, int id, List<ReportWrapper> sharedReports) {
        super(parent, id, NODE_NAME);
        this.reports = sharedReports;
    }

    @Override
    protected Collection<ReportWrapper> getReports() throws ApplicationException {
        return reports;
    }

    @Override
    protected int getStartNodeId() {
        return NODE_ID;
    }
}
