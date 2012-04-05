package edu.ualberta.med.biobank.common.action.exception;

import java.io.Serializable;

import edu.ualberta.med.biobank.i18n.SS;
import edu.ualberta.med.biobank.model.Name;

public class ModelNotFoundException extends ActionException {
    private static final long serialVersionUID = 1L;

    private final Class<?> modelClass;
    private final Serializable modelId;

    public ModelNotFoundException(Class<?> klazz, Serializable id) {
        super(getMessage(klazz, id));

        this.modelClass = klazz;
        this.modelId = id;
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    public Serializable getModelId() {
        return modelId;
    }

    @SuppressWarnings("nls")
    private static SS getMessage(Class<?> klazz, Serializable id) {
        SS name = Name.of(klazz);
        return SS.tr("Cannot find a {0} with id {1} in persistence.",
            new Object[] { name, id });
    }
}
