package edu.ualberta.med.biobank.export;

import java.util.List;

/**
 * Represents data to be exported by a <code>DataExporter</code>.
 * 
 * @author jferland
 * 
 */
public class Data {
    private String title;
    private List<String> description;
    private List<String> columnNames;
    private List<Object> rows;

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setRows(List<Object> rows) {
        this.rows = rows;
    }

    public List<?> getRows() {
        return rows;
    }
}