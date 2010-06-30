package edu.ualberta.med.biobank.common.reports2;

import java.io.Serializable;

public class Option implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected Class<?> type;
    private Object defaultValue;

    public Option(String name, Class<?> type, Object defaultValue) {
        this.name = name;
        this.type = type;
        this.setDefaultValue(defaultValue);
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
