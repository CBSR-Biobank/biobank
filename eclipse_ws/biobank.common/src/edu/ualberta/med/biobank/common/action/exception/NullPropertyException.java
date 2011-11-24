package edu.ualberta.med.biobank.common.action.exception;

import java.text.MessageFormat;

import edu.ualberta.med.biobank.common.wrappers.Property;

public class NullPropertyException extends ActionException {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE =
        "{0} cannot have a null value for property ''{1}''."; //$NON-NLS-1$
    private final Class<?> modelClass;
    private final String propertyName;

    public <E> NullPropertyException(Class<E> modelClass,
        Property<?, E> property) {
        this(modelClass, property.getName());
    }

    public NullPropertyException(Class<?> modelClass, String propertyName) {
        this.modelClass = modelClass;
        this.propertyName = propertyName;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format(MESSAGE,
            modelClass.getSimpleName(), propertyName);
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
