package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public abstract class LoadCheck<E> extends WrapperCheck<E> {
    private static final long serialVersionUID = 1L;

    private static final String CANNOT_LOAD_MSG = "Unable to load object type {0} with id ''{1}''.";

    protected LoadCheck(ModelWrapper<E> wrapper) {
        super(wrapper);
    }

    protected E loadModel(Session session) {
        @SuppressWarnings("unchecked")
        E loaded = (E) session.load(getModelClass(), getModelId());
        return loaded;
    }

    @Override
    public void doCheck(Session session) throws BiobankSessionException {
        E freshObject = loadModel(session);

        if (freshObject == null) {
            String modelClass = getModelClass().getSimpleName();
            String id = getModelId().toString();
            String msg = MessageFormat.format(CANNOT_LOAD_MSG, modelClass, id);

            throw new BiobankSessionException(msg);
        }

        doCheck(session, freshObject);
    }

    public abstract void doCheck(Session session, E freshObject)
        throws BiobankSessionException;
}
