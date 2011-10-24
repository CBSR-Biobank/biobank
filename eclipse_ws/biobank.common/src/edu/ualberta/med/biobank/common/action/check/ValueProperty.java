package edu.ualberta.med.biobank.common.action.check;

import edu.ualberta.med.biobank.common.wrappers.Property;

public class ValueProperty<T> {
    public Property<?, T> property;
    public Object value;

    public ValueProperty(Property<?, T> property, Object value) {
        this.property = property;
        this.value = value;
    }
}
