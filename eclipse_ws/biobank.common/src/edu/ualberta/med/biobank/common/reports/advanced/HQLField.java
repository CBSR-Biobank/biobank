package edu.ualberta.med.biobank.common.reports.advanced;

public class HQLField {
    private String fname;
    private Class<?> type;
    private String path;
    private Object value;
    private String operator;

    public HQLField(String path, String fname, Class<?> type) {
        this.fname = fname;
        this.type = type;
        this.path = path;
    }

    public HQLField(HQLField nodeInfo) {
        this.fname = new String(nodeInfo.getFname());
        this.type = nodeInfo.getType();
        this.path = new String(nodeInfo.getPath());
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setFname(String newName) {
        this.fname = newName;
    }

}
