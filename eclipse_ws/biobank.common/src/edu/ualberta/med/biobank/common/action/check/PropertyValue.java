package edu.ualberta.med.biobank.common.action.check;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.Property;

public class PropertyValue {
    private final String path;
    private final Object value;

    public PropertyValue(String path, Object value) {
        this.path = path;
        this.value = value;
    }

    public <E> PropertyValue(Property<E, ?> property, E value) {
        this(property.getName(), value);
    }

    public String getPath() {
        return path;
    }

    public Object getValue() {
        return value;
    }

    public static List<String> getPaths(List<PropertyValue> propertyValues) {
        List<String> propertyNames = new ArrayList<String>();

        for (PropertyValue propertyValue : propertyValues) {
            propertyNames.add(propertyValue.getPath());
        }

        return propertyNames;
    }
}
