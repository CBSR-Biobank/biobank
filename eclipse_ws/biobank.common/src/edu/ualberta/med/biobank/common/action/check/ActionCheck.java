package edu.ualberta.med.biobank.common.action.check;

import edu.ualberta.med.biobank.common.wrappers.Property;

public abstract class ActionCheck<T> {

    protected ValueProperty<? extends T> idProperty;
    protected Class<T> modelClass;

    protected ActionCheck(ValueProperty<? extends T> idProperty,
        Class<T> modelClass) {
        this.idProperty = idProperty;
        this.modelClass = modelClass;
    }

    protected Class<T> getModelClass() {
        return modelClass;
    }

    protected Integer getModelId() {
        return (Integer) idProperty.value;
    }

    protected Property<?, ? extends T> getIdProperty() {
        return idProperty.property;
    }
}
