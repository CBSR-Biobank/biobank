package edu.ualberta.med.biobank.client.reports.advanced;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.client.reports.IReport;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.BiobankListProxy;
import edu.ualberta.med.biobank.common.util.ReportOption;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class QueryObject implements IReport {

    /**
     * Description of this query object
     */
    private String description;

    /**
     * Query string of this query object
     */
    protected String queryString;

    /**
     * Column names for the result
     */
    protected String[] columnNames;

    protected List<ReportOption> queryOptions;

    public QueryObject(String description, String queryString,
        String[] columnNames) {
        this.description = description;
        this.queryString = queryString;
        queryOptions = new ArrayList<ReportOption>();
        this.columnNames = columnNames;
    }

    public void addOption(String name, Class<?> type, Object defaultValue) {
        queryOptions.add(new ReportOption(name, type, defaultValue));
    }

    @Override
    public String getDescription() {
        return description;
    }

    public List<Object> generate(WritableApplicationService appService,
        List<Object> params) throws ApplicationException, BiobankCheckException {
        return postProcess(appService,
            executeQuery(appService, preProcess(params)));
    }

    @SuppressWarnings("unused")
    protected List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException, BiobankCheckException {
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        return new BiobankListProxy(appService, c);
    }

    protected List<Object> preProcess(List<Object> params) {
        return params;
    }

    @SuppressWarnings("unused")
    protected List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        return results;
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public List<ReportOption> getOptions() {
        return queryOptions;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
