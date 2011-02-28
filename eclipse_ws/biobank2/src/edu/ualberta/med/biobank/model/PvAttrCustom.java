package edu.ualberta.med.biobank.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Deprecated
public class PvAttrCustom {
    private Boolean isDefault;
    private String label;
    private String type;
    private String[] allowedValues;
    private String value;

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
        this);

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        propertyChangeSupport.firePropertyChange("isDefault", this.isDefault,
            this.isDefault = isDefault);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        propertyChangeSupport.firePropertyChange("label", this.label,
            this.label = label);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        propertyChangeSupport.firePropertyChange("type", this.type,
            this.type = type);
    }

    public String[] getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(String[] allowedValues) {
        propertyChangeSupport.firePropertyChange("allowedValues",
            this.allowedValues, this.allowedValues = allowedValues);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        propertyChangeSupport.firePropertyChange("value", this.value,
            this.value = value);
    }

    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

};