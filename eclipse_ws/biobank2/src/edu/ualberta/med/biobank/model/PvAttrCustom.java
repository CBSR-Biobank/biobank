package edu.ualberta.med.biobank.model;

public class PvAttrCustom {
    private Boolean isDefault;
    private String label;
    private String type;
    private String[] allowedValues;
    private String value;

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(String[] allowedValues) {
        this.allowedValues = allowedValues;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
};