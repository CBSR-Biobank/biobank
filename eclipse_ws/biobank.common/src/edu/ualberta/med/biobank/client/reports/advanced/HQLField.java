package edu.ualberta.med.biobank.client.reports.advanced;

public class HQLField {
    private String fname;
    private Class<?> type;
    private String path;
    private Object value;
    private String operator;
    private Boolean display;

    public HQLField(String path, String fname, Class<?> type) {
        this.fname = fname;
        this.type = type;
        this.path = path;
        this.display = false;
    }

    public HQLField(HQLField nodeInfo) {
        this.fname = nodeInfo.getFname();
        this.type = nodeInfo.getType();
        this.path = nodeInfo.getPath();
        this.value = nodeInfo.getValue();
        this.operator = nodeInfo.getOperator();
        this.display = nodeInfo.getDisplay();
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

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public Boolean getDisplay() {
        return display;
    }

}
