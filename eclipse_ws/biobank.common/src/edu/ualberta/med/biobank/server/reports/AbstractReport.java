package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AbstractReport {

    protected static final String SENT_SAMPLES_FREEZER_NAME = "SS%";

    protected String queryString;

    protected List<Object> parameters;

    protected List<ReportOption> options;

    protected static final String SITE_OPERATOR = "$$siteOperator$$";

    protected static final String SITE_OPERATOR_SEARCH_STRING = replacePatternString(SITE_OPERATOR);

    protected static final String SITE_ID = "$$siteId$$";

    protected static final String SITE_ID_SEARCH_STRING = replacePatternString(SITE_ID);

    protected static final String GROUPBY_DATE = "$$groupBy$$";

    protected static final String GROUPBY_DATE_SEARCH_STRING = replacePatternString(GROUPBY_DATE);

    protected AbstractReport(String queryString, List<Object> parameters,
        List<ReportOption> options) {
        this.queryString = queryString;
        this.parameters = parameters;
        this.options = options;
    }

    public List<Object> generate(WritableApplicationService appService,
        String siteOperator, Integer siteId) throws ApplicationException {
        return postProcess(appService,
            executeQuery(appService, siteOperator, siteId));
    }

    protected List<Object> postProcess(
        @SuppressWarnings("unused") WritableApplicationService appService,
        List<Object> results) {
        return results;
    }

    public List<Object> executeQuery(WritableApplicationService appService,
        String siteOperator, Integer siteId) throws ApplicationException {
        queryString = queryString.replaceAll(SITE_OPERATOR_SEARCH_STRING,
            siteOperator);
        queryString = queryString.replaceAll(SITE_ID_SEARCH_STRING,
            siteId.toString());
        HQLCriteria criteria = new HQLCriteria(queryString, parameters);
        List<Object> results = appService.query(criteria);
        return results;
    }

    private static String replacePatternString(String pattern) {
        return pattern.replaceAll("\\$", "\\\\\\$");
    }

}
