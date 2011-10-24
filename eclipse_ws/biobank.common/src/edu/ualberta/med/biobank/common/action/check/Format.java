package edu.ualberta.med.biobank.common.action.check;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.Property;

class Format {
    private static final String DELIMITER = ", "; //$NON-NLS-1$

    static String modelClass(Class<?> modelClass) {
        // TODO: some formatting? language translation lookup?
        return modelClass.getSimpleName();
    }

    static <E> String propertyValues(
        Collection<ValueProperty<E>> valueProperties) {
        StringBuilder sb = new StringBuilder();
        int n = valueProperties.size();
        int i = 0;
        for (ValueProperty<E> vp : valueProperties) {
            sb.append(vp.value == null ? "null" : vp.value.toString()); //$NON-NLS-1$
            i++;
            if (i < n) {
                sb.append(DELIMITER);
            }
        }
        return sb.toString();
    }

    static <E> String propertyNames(Collection<ValueProperty<E>> valueProperties) {
        StringBuilder sb = new StringBuilder();
        int n = valueProperties.size();
        int i = 0;
        for (ValueProperty<E> vp : valueProperties) {
            sb.append(propertyName(vp.property));
            i++;
            if (i < n) {
                sb.append(DELIMITER);
            }
        }
        return sb.toString();
    }

    static String propertyName(Property<?, ?> property) {
        // TODO: some formatting? language translation lookup?
        // TODO: what about things like address.city? lookup probably best.
        return property.getName();
    }
}