package edu.ualberta.med.biobank.model;

public class ReportFilterValue extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer position;
    private String value;
    private String secondValue;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(String secondValue) {
        this.secondValue = secondValue;
    }
}
