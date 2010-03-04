package edu.ualberta.med.biobank.common.reports.advanced;

public class HQLField {
    private String fname;
    private Class<?> type;
    private String path;

    public HQLField(String path, String fname, Class<?> type) {
        this.fname = fname;
        this.type = type;
        this.path = path;
    }

    public String getFname() {
        return fname;
    }

    public Class<?> getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

}
