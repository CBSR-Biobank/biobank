package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.ReportListProxy;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AbstractReport {

    protected static final String SENT_SAMPLES_FREEZER_NAME = "SS%";

    protected String queryString;

    protected BiobankReport report;

    protected static final String SITE_OPERATOR = "$$siteOperator$$";

    protected static final String SITE_OPERATOR_SEARCH_STRING = replacePatternString(SITE_OPERATOR);

    protected static final String SITE_ID = "$$siteId$$";

    protected static final String SITE_ID_SEARCH_STRING = replacePatternString(SITE_ID);

    protected static final String GROUPBY_DATE = "$$groupBy$$";

    protected static final String GROUPBY_DATE_SEARCH_STRING = replacePatternString(GROUPBY_DATE);

    protected static final String CONTAINER_LIST = "$$containerList$$";

    protected static final String CONTAINER_LIST_SEARCH_STRING = replacePatternString(CONTAINER_LIST);

    protected AbstractReport(String queryString, BiobankReport report) {
        this.queryString = queryString;
        this.report = report;
    }

    public List<Object> generate(WritableApplicationService appService)
        throws ApplicationException {
        return postProcess(appService, executeQuery(appService));
    }

    /**
     * Post process the whole collection after its retrieval
     */
    protected List<Object> postProcess(
        @SuppressWarnings("unused") WritableApplicationService appService,
        List<Object> results) {
        return results;
    }

    @SuppressWarnings("unused")
    public List<Object> executeQuery(WritableApplicationService appService)
        throws ApplicationException {
        queryString = queryString.replaceAll(SITE_OPERATOR_SEARCH_STRING,
            report.getOp());
        queryString = queryString.replaceAll(SITE_ID_SEARCH_STRING, report
            .getSiteId().toString());
        queryString = queryString.replaceAll(GROUPBY_DATE_SEARCH_STRING,
            report.getGroupBy());
        queryString = queryString.replaceAll(CONTAINER_LIST_SEARCH_STRING,
            report.getContainerList());
        HQLCriteria criteria = new HQLCriteria(queryString, report.getParams());
        return new ReportListProxy(appService, criteria, getRowPostProcess());
    }

    /**
     * Will process line by line on the client side (if is called from the
     * client side)
     */
    protected AbstractRowPostProcess getRowPostProcess() {
        return null;
    }

    private static String replacePatternString(String pattern) {
        return pattern.replaceAll("\\$", "\\\\\\$");
    }

}
