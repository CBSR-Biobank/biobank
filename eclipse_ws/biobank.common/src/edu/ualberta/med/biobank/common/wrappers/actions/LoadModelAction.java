package edu.ualberta.med.biobank.common.wrappers.actions;

import java.text.MessageFormat;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public abstract class LoadModelAction<E> extends UncachedAction<E> {
    private static final long serialVersionUID = 1L;

    private static final String CANNOT_LOAD_MSG = "Unable to load object type {0} with id ''{1}''."; //$NON-NLS-1$

    public LoadModelAction(ModelWrapper<E> wrapper) {
        super(wrapper);
    }

    protected E loadModel(Session session) {
        E loaded = null;

        Integer id = getModelId();

        if (id != null) {
            @SuppressWarnings("unchecked")
            E tmp = (E) session.load(getModelClass(), id);
            loaded = tmp;
            // session.setReadOnly(loaded, true);
        }

        return loaded;
    }

    @Override
    public final void doUncachedAction(Session session)
        throws BiobankSessionException {
        E loadModel = loadModel(session);

        Integer id = getModelId();
        if (id != null && loadModel == null) {
            String modelClass = getModelClass().getSimpleName();
            String msg = MessageFormat.format(CANNOT_LOAD_MSG, modelClass, id);

            throw new BiobankSessionException(msg);
        }

        doLoadModelAction(session, loadModel);
    }

    public abstract void doLoadModelAction(Session session, E loadedModel)
        throws BiobankSessionException;
}
