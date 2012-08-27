package edu.ualberta.med.biobank.common.action.exception;

import java.io.Serializable;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Name;

public class ModelNotFoundException extends ActionException {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    private static final Tr MESSAGE =
        bundle.tr("Cannot find a {0} with id {1} in persistence.");

    private final Class<?> modelClass;
    private final Serializable modelId;

    public ModelNotFoundException(Class<?> klazz, Serializable id,
        Throwable cause) {
        super(MESSAGE.format(Name.of(klazz), id), cause);

        this.modelClass = klazz;
        this.modelId = id;
    }

    public ModelNotFoundException(Class<?> klazz, Serializable id) {
        this(klazz, id, null);
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    public Serializable getModelId() {
        return modelId;
    }
}
