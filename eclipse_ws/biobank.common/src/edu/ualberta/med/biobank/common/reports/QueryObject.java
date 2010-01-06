package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class QueryObject {

    /**
     * Description of this query object
     */
    private String description;

    /**
     * Name of this query object
     */
    private String name;

    /**
     * Query string of this query object
     */
    protected String queryString;

    /**
     * Column names for the result
     */
    private String[] columnNames;

    protected List<Option> queryOptions;

    public class Option {
        protected String name;
        protected Class<?> type;
        protected Object defaultValue;

        public Option(String name, Class<?> type, Object defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }
    }

    public QueryObject(String description, String name, String queryString,
        String[] columnNames) {
        this.description = description;
        this.name = name;
        this.queryString = queryString;
        queryOptions = new ArrayList<Option>();
        this.columnNames = columnNames;
    }

    public void addOption(String name, Class<?> type, Object defaultValue) {
        queryOptions.add(new Option(name, type, defaultValue));
    }

    public static List<QueryObject> getAllQueries(Integer siteId) {
        ArrayList<QueryObject> queries = new ArrayList<QueryObject>();

        // create all pre-defined queries here
        QueryObject invoicePQuery = new SampleInvoiceByPatient(
            "SampleInvoiceByPatient", siteId);
        QueryObject invoiceCQuery = new SampleInvoiceByClinic(
            "SampleInvoiceByClinic", siteId);
        QueryObject sampleCountQuery = new SampleCountQueryObject(
            "SampleCount", siteId);
        QueryObject sampleSCountQuery = new SampleSCountQueryObject(
            "SampleCountByStudy", siteId);
        QueryObject fvlPatientVisitsQuery = new FvLPatientVisitsQueryObject(
            "FirstVsLastPatientVisitsByClinic", siteId);
        QueryObject freezerSSamplesQuery = new FreezerSSamplesQueryObject(
            "FreezerSamplesByStudy", siteId);
        QueryObject freezerCSamplesQuery = new FreezerCSamplesQueryObject(
            "FreezerSamplesByStudyClinic", siteId);
        QueryObject freezerDSamplesQuery = new FreezerDSamplesQueryObject(
            "FreezerSamplesByDate", siteId);
        QueryObject patientWBC = new PatientWBC("PatientWBC", siteId);
        QueryObject patientVisitSummary = new PatientVisitSummary(
            "PatientVisitSummary", siteId);

        queries.add(invoicePQuery);
        queries.add(invoiceCQuery);
        queries.add(sampleCountQuery);
        queries.add(sampleSCountQuery);
        queries.add(fvlPatientVisitsQuery);
        queries.add(freezerSSamplesQuery);
        queries.add(freezerCSamplesQuery);
        queries.add(freezerDSamplesQuery);
        queries.add(patientWBC);
        queries.add(patientVisitSummary);

        return queries;
    }

    public String getDescription() {
        return description;
    }

    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        for (int i = 0; i < queryOptions.size(); i++) {
            Option option = queryOptions.get(i);
            if (params.get(i) == null)
                params.set(i, option.defaultValue);
            if (option.type.equals(String.class))
                params.set(i, "%" + params.get(i) + "%");
        }
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = appService.query(c);
        return postProcess(results);
    }

    public List<Object> postProcess(List<Object> results) {
        return results;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public List<Option> getOptions() {
        return queryOptions;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
