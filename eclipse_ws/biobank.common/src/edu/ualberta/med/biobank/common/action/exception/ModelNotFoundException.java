package edu.ualberta.med.biobank.common.action.exception;

import java.io.Serializable;
import java.text.MessageFormat;

public class ModelNotFoundException extends ActionException {
    private static final long serialVersionUID = 1L;
    private static final String MESSAGE =
        "Cannot find model of type {1} with id {1} in persistence.";

    private final Class<?> modelClass;
    private final Serializable modelId;

    public ModelNotFoundException(Class<?> modelClass, Serializable modelId) {
        super(getMessage(modelClass, modelId));

        this.modelClass = modelClass;
        this.modelId = modelId;
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    public Serializable getModelId() {
        return modelId;
    }

    private static String getMessage(Class<?> modelClass, Serializable modelId) {
        String message = MessageFormat.format(MESSAGE, modelClass, modelId);
        return message;
    }
}
