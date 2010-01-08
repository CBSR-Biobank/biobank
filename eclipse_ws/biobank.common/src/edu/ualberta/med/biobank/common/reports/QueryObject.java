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

    public static List<Class<? extends QueryObject>> getAllQueries() {
        ArrayList<Class<? extends QueryObject>> queries = new ArrayList<Class<? extends QueryObject>>();

        // create all pre-defined queries here

        queries.add(FreezerCSamples.class);
        queries.add(FreezerDSamples.class);
        queries.add(FreezerSSamples.class);
        queries.add(FvLPatientVisits.class);
        queries.add(PatientVisitSummary.class);
        queries.add(PatientWBC.class);
        queries.add(SampleCount.class);
        queries.add(SampleInvoiceByClinic.class);
        queries.add(SampleInvoiceByPatient.class);
        queries.add(SampleSCount.class);

        return queries;
    }

    public String getDescription() {
        return description;
    }

    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        // queryOptions.add(SessionManager.)
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
