package edu.ualberta.med.biobank.views;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.widgets.AttributeQueryClause;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class QueryObject {

    private List<Class<?>> fieldTypes;
    private String description;
    private String queryString;
    private List<String> fieldNames;
    private Class<?> objectType;

    public QueryObject(Class<?> objectType, String description,
        String queryString, List<Class<?>> fieldTypes, List<String> fieldNames) {
        this.objectType = objectType;
        this.description = description;
        this.queryString = queryString;
        this.fieldTypes = fieldTypes;
        this.fieldNames = fieldNames;
    }

    public static List<QueryObject> getAllQueries() {
        ArrayList<QueryObject> queries = new ArrayList<QueryObject>();

        // create all pre-defined queries here
        ArrayList<Class<?>> fieldList = new ArrayList<Class<?>>();
        fieldList.add(String.class);
        ArrayList<String> nameList = new ArrayList<String>();
        nameList.add("ContainerType Name");

        String objName = AttributeQueryClause.getText(Container.class);

        QueryObject testQuery = new QueryObject(
            ContainerWrapper.class,
            "Retrieves a list of all containers that have a type named similarly to:",
            "Select " + objName + "Alias from " + Container.class.getName()
                + " as " + objName + "Alias where " + objName
                + "Alias.containerType.name like ?", fieldList, nameList);

        fieldList = new ArrayList<Class<?>>();
        fieldList.add(String.class);
        fieldList.add(String.class);
        nameList = new ArrayList<String>();
        nameList.add("Site Name");
        nameList.add("Study Name");
        objName = AttributeQueryClause.getText(Patient.class);
        QueryObject testQuery2 = new QueryObject(PatientWrapper.class,
            "Retrieves a list of patients belonging to a given study and site",
            "Select " + objName + "Alias from " + Patient.class.getName()
                + " as " + objName + "Alias where " + objName
                + "Alias.study.site.name like ? and " + objName
                + "Alias.study.name like ?", fieldList, nameList);

        queries.add(testQuery);
        queries.add(testQuery2);
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
            if (fieldTypes.get(i).equals(String.class))
                params.set(i, "%" + params.get(i) + "%");
        }
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = new ArrayList<Object>();
        try {
            results = SessionManager.getAppService().query(c);
        } catch (Exception e) {
            ReportsView.LOGGER.error("HQLQuery failed: " + c.toString() + "\n"
                + e.toString());
        }
        return results;
    }

    public Class<?> getReturnType() {
        return objectType;
    }

    @Override
    public String toString() {
        return objectType.getName() + " Query";
    }
}
