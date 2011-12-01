package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.common.wrappers.Property;

public class ModelIsUsedException extends ActionException {
    private static final long serialVersionUID = 1L;
    private final Class<?> modelClass;
    private final Integer modelId;
    private final Property<?, ?> byProperty;

    public <T, U> ModelIsUsedException(Class<T> modelClass, Integer modelId,
        Property<? super T, U> byProperty) {
        this.modelClass = modelClass;
        this.modelId = modelId;
        this.byProperty = byProperty;
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    public Integer getModelId() {
        return modelId;
    }

    public Property<?, ?> getByProperty() {
        return byProperty;
    }
}
