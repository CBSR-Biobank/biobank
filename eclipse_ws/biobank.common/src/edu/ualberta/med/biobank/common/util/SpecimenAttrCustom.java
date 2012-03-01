package edu.ualberta.med.biobank.common.util;

import edu.ualberta.med.biobank.common.wrappers.SpecimenAttrTypeEnum;

public class SpecimenAttrCustom {
    private Boolean isDefault;
    private String label;
    private SpecimenAttrTypeEnum type;
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

    public SpecimenAttrTypeEnum getType() {
        return type;
    }

    public void setType(SpecimenAttrTypeEnum type) {
        this.type = type;
    }

    public void setType(String typeName) {
        setType(SpecimenAttrTypeEnum.getSpecimenAttrType(typeName));
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