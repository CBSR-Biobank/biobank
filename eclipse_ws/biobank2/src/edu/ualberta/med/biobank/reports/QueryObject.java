package edu.ualberta.med.biobank.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class QueryObject {

    protected List<Class<?>> fieldTypes;
    private String description;
    private String queryString;
    protected List<String> fieldNames;
    private String name;
    private String[] columnNames;
    protected List<Object> defaults;

    public QueryObject(String description, String name, String queryString,
        List<Class<?>> fieldTypes, List<Object> defaults,
        List<String> fieldNames, String[] columnNames) {
        this.description = description;
        this.name = name;
        this.queryString = queryString;
        this.fieldTypes = fieldTypes;
        this.defaults = defaults;
        this.fieldNames = fieldNames;
        this.columnNames = columnNames;
    }

    public static List<QueryObject> getAllQueries() {
        ArrayList<QueryObject> queries = new ArrayList<QueryObject>();

        // create all pre-defined queries here

        QueryObject invoicePQuery = new InvoicePQueryObject(
            "SampleInvoiceByPatient");
        QueryObject invoiceCQuery = new InvoiceCQueryObject(
            "SampleInvoiceByClinic");

        queries.add(invoicePQuery);
        queries.add(invoiceCQuery);

        return queries;
    }

    public String getDescription() {
        return description;
    }

    public List<Class<?>> getFieldTypes() {
        return fieldTypes;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public List<Object> executeQuery(List<Object> params) {
        for (int i = 0; i < fieldTypes.size(); i++) {
            if (params.get(i) == null)
                params.set(i, defaults.get(i));
            if (fieldTypes.get(i).equals(String.class))
                params.set(i, "%" + params.get(i) + "%");
        }
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = new ArrayList<Object>();
        try {
            results = SessionManager.getAppService().query(c);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        return postProcess(results);
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Object> postProcess(List<Object> results) {
        return results;
    }

    public String[] getColumnNames() {
        return columnNames;
    }
}
