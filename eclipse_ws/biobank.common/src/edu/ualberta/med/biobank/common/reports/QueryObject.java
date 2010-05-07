package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class QueryObject {

    private static Map<String, Class<? extends QueryObject>> QUERIES = new TreeMap<String, Class<? extends QueryObject>>();

    static {
        Map<String, Class<? extends QueryObject>> aMap = new TreeMap<String, Class<? extends QueryObject>>();
        aMap.put(CabinetCAliquots.NAME, CabinetCAliquots.class);
        aMap.put(CabinetDAliquots.NAME, CabinetDAliquots.class);
        aMap.put(CabinetSAliquots.NAME, CabinetSAliquots.class);
        aMap.put(FreezerCAliquots.NAME, FreezerCAliquots.class);
        aMap.put(FreezerDAliquots.NAME, FreezerDAliquots.class);
        aMap.put(FreezerSAliquots.NAME, FreezerSAliquots.class);
        aMap.put(FvLPatientVisits.NAME, FvLPatientVisits.class);
        aMap.put(NewPVsByStudyClinic.NAME, NewPVsByStudyClinic.class);
        aMap.put(NewPsByStudyClinic.NAME, NewPsByStudyClinic.class);
        aMap.put(PsByStudy.NAME, PsByStudy.class);
        aMap.put(PVsByStudy.NAME, PVsByStudy.class);
        aMap.put(PatientVisitSummary.NAME, PatientVisitSummary.class);
        aMap.put(PatientWBC.NAME, PatientWBC.class);
        aMap.put(AliquotCount.NAME, AliquotCount.class);
        aMap.put(AliquotInvoiceByClinic.NAME, AliquotInvoiceByClinic.class);
        aMap.put(AliquotInvoiceByPatient.NAME, AliquotInvoiceByPatient.class);
        aMap.put(AliquotRequest.NAME, AliquotRequest.class);
        aMap.put(AliquotSCount.NAME, AliquotSCount.class);
        aMap.put(SampleTypePvCount.NAME, SampleTypePvCount.class);
        aMap.put(SampleTypeSUsage.NAME, SampleTypeSUsage.class);
        aMap.put(QACabinetAliquots.NAME, QACabinetAliquots.class);
        aMap.put(QAFreezerAliquots.NAME, QAFreezerAliquots.class);
        QUERIES = Collections.unmodifiableMap(aMap);
    };

    public enum DateGroup {
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
        List<Object> params) throws ApplicationException {
        return postProcess(executeQuery(appService, preProcess(params)));
    }

    protected List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        return new BiobankListProxy(appService, c);
    }

    protected List<Object> preProcess(List<Object> params) {
        return params;
    }

    protected List<Object> postProcess(List<Object> results) {
        return results;
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

}
