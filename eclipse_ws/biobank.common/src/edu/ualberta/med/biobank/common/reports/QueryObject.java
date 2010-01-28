package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class QueryObject {

    private static Map<String, Class<? extends QueryObject>> QUERIES = new TreeMap<String, Class<? extends QueryObject>>();

    static {
        Map<String, Class<? extends QueryObject>> aMap = new TreeMap<String, Class<? extends QueryObject>>();
        aMap.put(CabinetDSamples.NAME, CabinetDSamples.class);
        aMap.put(NewPVsByStudyClinic.NAME, NewPVsByStudyClinic.class);
        aMap.put(PatientVisitSummary.NAME, PatientVisitSummary.class);
        aMap.put(SampleCount.NAME, SampleCount.class);
        aMap.put(FreezerCSamples.NAME, FreezerCSamples.class);
        aMap.put(FreezerSSamples.NAME, FreezerSSamples.class);
        aMap.put(CabinetSSamples.NAME, CabinetSSamples.class);
        aMap.put(FvLPatientVisits.NAME, FvLPatientVisits.class);
        aMap.put(FreezerDSamples.NAME, FreezerDSamples.class);
        aMap.put(SampleSCount.NAME, SampleSCount.class);
        aMap.put(CabinetCSamples.NAME, CabinetCSamples.class);
        aMap.put(SampleInvoiceByClinic.NAME, SampleInvoiceByClinic.class);
        aMap.put(PatientWBC.NAME, PatientWBC.class);
        aMap.put(SampleRequest.NAME, SampleRequest.class);
        aMap.put(NewPsByStudyClinic.NAME, NewPsByStudyClinic.class);
        aMap.put(SampleInvoiceByPatient.NAME, SampleInvoiceByPatient.class);
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

    public static Map<String, Class<? extends QueryObject>> getAllQueries() {
        return QUERIES;
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

    public List<Object> executeQuery(WritableApplicationService appService,
        List<Object> params) throws ApplicationException {
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    // These methodss can used in a typical date range summation

    public List<Object> sumByDate(List<Object> results) {
        if (results.size() == 0)
            return results;
        List<Object> totalledResults = new ArrayList<Object>();

        int upperBound = 1, lowerBound = 0;
        // weakly typed, consider changing to enum with method to protect this
        // variable from being mis-set.. but might not be a big deal
        int CALENDAR_TYPE;
        int incrementBy;

        if (columnNames[0].compareTo("Week") == 0) {
            CALENDAR_TYPE = Calendar.WEEK_OF_YEAR;
            incrementBy = 1;
        } else if (columnNames[0].compareTo("Month") == 0) {
            CALENDAR_TYPE = Calendar.MONTH;
            incrementBy = 1;
        } else if (columnNames[0].compareTo("Quarter") == 0) {
            CALENDAR_TYPE = Calendar.MONTH;
            incrementBy = 4;
        } else {
            CALENDAR_TYPE = Calendar.YEAR;
            incrementBy = 1;
        }

        String lastStudy = (String) ((Object[]) results.get(0))[1], lastClinic = (String) ((Object[]) results
            .get(0))[2];
        for (Object obj : results) {
            Object[] castObj = (Object[]) obj;
            if (((String) castObj[1]).compareTo(lastStudy) != 0
                || ((String) castObj[2]).compareTo(lastClinic) != 0) {
                totalledResults.addAll(sumSection(results, lowerBound,
                    upperBound, CALENDAR_TYPE, incrementBy, lastStudy,
                    lastClinic));
                lastStudy = (String) castObj[1];
                lastClinic = (String) castObj[2];
                lowerBound = upperBound;
            }
            upperBound++;
        }
        totalledResults.addAll(sumSection(results, lowerBound, upperBound - 1,
            CALENDAR_TYPE, incrementBy, lastStudy, lastClinic));
        return totalledResults;
    }

    private List<Object> sumSection(List<Object> results, int lowerbound,
        int upperbound, int CALENDAR_TYPE, int incrementBy, String study,
        String clinic) {

        if (results.size() == 0 || upperbound - lowerbound < 1)
            return results;

        int count = 0, grpNumber = 1;
        List<Object> totalledResults = new ArrayList<Object>();
        Calendar start = Calendar.getInstance();
        start.setTime((Date) ((Object[]) results.get(lowerbound))[0]);
        Calendar end = Calendar.getInstance();
        end.setTime(start.getTime());
        end.add(CALENDAR_TYPE, incrementBy);
        Calendar date = Calendar.getInstance();
        Iterator<Object> it = results.listIterator(lowerbound);
        for (int i = lowerbound; i < upperbound; i++) {
            Object obj = it.next();
            Object[] castObj = (Object[]) obj;
            date.setTime((Date) castObj[0]);
            if (date.compareTo(start) >= 0 && date.compareTo(end) <= 0) {
                count++;
            } else {
                totalledResults.add(new Object[] { grpNumber, study, clinic,
                    count });
                while (date.compareTo(end) >= 0) {
                    start.add(CALENDAR_TYPE, incrementBy);
                    end.add(CALENDAR_TYPE, incrementBy);
                    grpNumber++;
                }
                count = 1;
            }
        }
        if (count > 1)
            totalledResults
                .add(new Object[] { grpNumber, study, clinic, count });
        return totalledResults;
    }

}
