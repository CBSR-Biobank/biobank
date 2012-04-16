package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.PostProcessListProxy;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

@SuppressWarnings("nls")
public class AbstractReport {

    protected static final String SENT_SAMPLES_FREEZER_NAME = "SS%";

    protected String queryString;

    protected BiobankReport report;

    protected static final String SITE_OPERATOR = "$$siteOperator$$";

    protected static final String SITE_OPERATOR_SEARCH_STRING =
        replacePatternString(SITE_OPERATOR);

    protected static final String SITE_ID = "$$siteId$$";

    protected static final String SITE_ID_SEARCH_STRING =
        replacePatternString(SITE_ID);

    protected static final String GROUPBY_DATE = "$$groupBy$$";

    protected static final String GROUPBY_DATE_SEARCH_STRING =
        replacePatternString(GROUPBY_DATE);

    protected static final String CONTAINER_LIST = "$$containerList$$";

    protected static final String CONTAINER_LIST_SEARCH_STRING =
        replacePatternString(CONTAINER_LIST);

    public static final String FTA_CARD_SAMPLE_TYPE_NAME = "DNA(Blood)";

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
    @SuppressWarnings("unused")
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        return results;
    }

    @SuppressWarnings("unused")
    public List<Object> executeQuery(WritableApplicationService appService)
        throws ApplicationException {
        if (report.getOp() != null)
            queryString = queryString.replaceAll(SITE_OPERATOR_SEARCH_STRING,
                report.getOp());
        if (report.getSiteId() != null)
            queryString = queryString.replaceAll(SITE_ID_SEARCH_STRING, report
                .getSiteId().toString());
        if (report.getGroupBy() != null)
            queryString = queryString.replaceAll(GROUPBY_DATE_SEARCH_STRING,
                report.getGroupBy());
        if (report.getContainerList() != null)
            queryString = queryString.replaceAll(CONTAINER_LIST_SEARCH_STRING,
                report.getContainerList());
        HQLCriteria criteria = new HQLCriteria(queryString, report.getParams());
        return new PostProcessListProxy<Object>(appService, criteria,
            getRowPostProcess());
    }

    /**
     * Will process line by line on the client side (if is called from the
     * client side)
     */
    public AbstractRowPostProcess getRowPostProcess() {
        return null;
    }

    private static String replacePatternString(String pattern) {
        return pattern.replaceAll("\\$", "\\\\\\$");
    }

}
