package edu.ualberta.med.biobank.common.wrappers.checks;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

// TODO: write description
public abstract class BiobankWrapperOnSavedCheck<E> extends
    BiobankWrapperCheck<E> {
    private static final long serialVersionUID = 1L;

    protected BiobankWrapperOnSavedCheck(ModelWrapper<E> wrapper) {
        super(wrapper);
    }

    @Override
    public final void doCheck(Session session) throws BiobankSessionException {
        // TODO: write a test that persists a model object, then does a
        // CheckLoad on it, then persists, etc. to check if hib throws
        // exceptions
        E loaded = loadSavedModel(session);
        doCheckLoaded(session, loaded);
    }

    protected E loadSavedModel(Session session) {
        @SuppressWarnings("unchecked")
        E loaded = (E) session.load(getModelClass(), getModelId());
        return loaded;
    }

    public abstract void doCheckLoaded(Session session, E loaded)
        throws BiobankSessionException;
}
