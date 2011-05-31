package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;

public abstract class BiobankCheck<E> extends BiobankWrapperAction<E> {
    private static final long serialVersionUID = 1L;

    private final String modelString;

    protected BiobankCheck(ModelWrapper<E> wrapper) {
        super(wrapper);
        this.modelString = wrapper.toString();
    }

    protected String getModelString() {
        return modelString;
    }

    protected static class Format {
        private static final String DELIMITER = ", ";

        public static String modelClass(Class<?> modelClass) {
            // TODO: some formatting? language translation lookup?
            return modelClass.getSimpleName();
        }

        public static <E> String propertyValues(E model,
            Collection<Property<?, E>> properties) {
            StringBuilder sb = new StringBuilder();
            int n = properties.size();
            int i = 0;
            for (Property<?, E> property : properties) {
                sb.append(property.get(model).toString());
                if (i < n) {
                    sb.append(DELIMITER);
                }
                i++;
            }
            return sb.toString();
        }

        public static <E> String propertyNames(
            Collection<Property<?, E>> properties) {
            StringBuilder sb = new StringBuilder();
            int n = properties.size();
            int i = 0;
            for (Property<?, E> property : properties) {
                sb.append(propertyName(property));
                if (i < n) {
                    sb.append(DELIMITER);
                }
                i++;
            }
            return sb.toString();
        }

        public static String propertyName(Property<?, ?> property) {
            // TODO: some formatting? language translation lookup?
            // TODO: what about things like address.city? lookup probably best.
            return property.getName();
        }
    }
}
