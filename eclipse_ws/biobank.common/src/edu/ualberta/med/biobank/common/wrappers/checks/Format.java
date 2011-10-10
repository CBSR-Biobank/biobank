package edu.ualberta.med.biobank.common.wrappers.checks;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.Property;

class Format {
    private static final String DELIMITER = ", "; //$NON-NLS-1$

    static String modelClass(Class<?> modelClass) {
        // TODO: some formatting? language translation lookup?
        return modelClass.getSimpleName();
    }

    static <E> String propertyValues(E model,
        Collection<Property<?, ? super E>> properties) {
        StringBuilder sb = new StringBuilder();
        int n = properties.size();
        int i = 0;
        for (Property<?, ? super E> property : properties) {
            Object pValue = property.get(model);
            sb.append(pValue == null ? "null" : pValue.toString()); //$NON-NLS-1$
            i++;
            if (i < n) {
                sb.append(DELIMITER);
            }
        }
        return sb.toString();
    }

    static <E> String propertyNames(
        Collection<Property<?, ? super E>> properties) {
        StringBuilder sb = new StringBuilder();
        int n = properties.size();
        int i = 0;
        for (Property<?, ? super E> property : properties) {
            sb.append(propertyName(property));
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