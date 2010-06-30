package edu.ualberta.med.biobank.common.reports2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Description of this query object
     */
    private String description;

    /**
     * Column names for the result
     */
    private String[] columnNames;

    private List<Option> queryOptions;

    private Object[] parameters;

    public AbstractReport(String description, String[] columnNames) {
        this.description = description;
        queryOptions = new ArrayList<Option>();
        this.columnNames = columnNames;
    }

    public abstract String getName();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<Option> getOptions() {
        return queryOptions;
    }

    public void addOption(String name, Class<?> type, Object defaultValue) {
        queryOptions.add(new Option(name, type, defaultValue));
    }

    public void setOptions(List<Option> queryOptions) {
        this.queryOptions = queryOptions;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
