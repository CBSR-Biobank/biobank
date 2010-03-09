package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.proxy.ListProxy;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class QueryObject {

    private static Map<String, Class<? extends QueryObject>> QUERIES = new TreeMap<String, Class<? extends QueryObject>>();

    static {
        Map<String, Class<? extends QueryObject>> aMap = new TreeMap<String, Class<? extends QueryObject>>();
        aMap.put(CabinetCSamples.NAME, CabinetCSamples.class);
        aMap.put(CabinetDSamples.NAME, CabinetDSamples.class);
        aMap.put(CabinetSSamples.NAME, CabinetSSamples.class);
        aMap.put(FreezerCSamples.NAME, FreezerCSamples.class);
        aMap.put(FreezerDSamples.NAME, FreezerDSamples.class);
        aMap.put(FreezerSSamples.NAME, FreezerSSamples.class);
        aMap.put(FvLPatientVisits.NAME, FvLPatientVisits.class);
        aMap.put(NewPVsByStudyClinic.NAME, NewPVsByStudyClinic.class);
        aMap.put(NewPsByStudyClinic.NAME, NewPsByStudyClinic.class);
        aMap.put(PatientVisitSummary.NAME, PatientVisitSummary.class);
        aMap.put(PatientWBC.NAME, PatientWBC.class);
        aMap.put(SampleCount.NAME, SampleCount.class);
        aMap.put(SampleInvoiceByClinic.NAME, SampleInvoiceByClinic.class);
        aMap.put(SampleInvoiceByPatient.NAME, SampleInvoiceByPatient.class);
        aMap.put(SampleRequest.NAME, SampleRequest.class);
        aMap.put(SampleSCount.NAME, SampleSCount.class);
        aMap.put(QACabinetSamples.NAME, QACabinetSamples.class);
        aMap.put(QAFreezerSamples.NAME, QAFreezerSamples.class);
        QUERIES = Collections.unmodifiableMap(aMap);
    };

    public enum DateRange {
        Week, Month, Quarter, Year
    }

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

    protected List<Option> queryOptions;

    public class Option {
        protected String name;
        protected Class<?> type;
        private Object defaultValue;

        public Option(String name, Class<?> type, Object defaultValue) {
            this.name = name;
            this.type = type;
            this.setDefaultValue(defaultValue);
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public QueryObject(String description, String queryString,
        String[] columnNames) {
        this.description = description;
        this.queryString = queryString;
        queryOptions = new ArrayList<Option>();
        this.columnNames = columnNames;
    }

    public void addOption(String name, Class<?> type, Object defaultValue) {
        queryOptions.add(new Option(name, type, defaultValue));
    }

    public static String[] getQueryObjectNames() {
        return QUERIES.keySet().toArray(new String[] {});
    }

    public static Class<? extends QueryObject> getQueryObjectByName(String name)
        throws Exception {
        Class<? extends QueryObject> queryObject = QUERIES.get(name);
        if (queryObject == null) {
            throw new Exception("Query object \"" + name + "\" does not exist");
        }
        return queryObject;
    }

    public String getDescription() {
        return description;
    }

    public List<Object> generate(WritableApplicationService appService,
        List<Object> params) {
        List<Object> results = null;
        try {
            results = postProcess(((ListProxy) executeQuery(appService, params))
                .getListChunk());
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return results;
    }

    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        return appService.query(c);
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public List<Option> getOptions() {
        return queryOptions;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public abstract String getName();

    public List<Object> postProcess(List<Object> results) {
        return results;
    }

}
