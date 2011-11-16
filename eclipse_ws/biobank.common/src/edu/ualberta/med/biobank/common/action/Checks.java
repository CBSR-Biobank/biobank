package edu.ualberta.med.biobank.common.action;

import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.wrappers.Property;

public class Checks {
    public <E> void notNull(E object, Property<?, ? super E> property)
        throws NullPropertyException {
        if (object == null || property.get(object) == null) {
            // throw new NullPropertyException(object.getClass(), property);
        }
    }

    public <E> void notNullValue(E value, Property<E, ?> property)
        throws NullPropertyException {
        if (value == null) {
            // throw new NullPropertyException(property.getModelClass(),
            // property);
        }
    }
}
