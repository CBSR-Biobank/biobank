package edu.ualberta.med.biobank.client.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.reports2.ReportOption;

public abstract class AbstractReport {

    /**
     * Description of this query object
     */
    private String description;

    /**
     * Column names for the result
     */
    private String[] columnNames;

    private List<ReportOption> queryOptions;

    public AbstractReport(String description, String[] columnNames) {
        this.description = description;
        queryOptions = new ArrayList<ReportOption>();
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

    public List<ReportOption> getOptions() {
        return queryOptions;
    }

    public void addOption(String name, Class<?> type, Object defaultValue) {
        queryOptions.add(new ReportOption(name, type, defaultValue));
    }

    public void setOptions(List<ReportOption> queryOptions) {
        this.queryOptions = queryOptions;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
