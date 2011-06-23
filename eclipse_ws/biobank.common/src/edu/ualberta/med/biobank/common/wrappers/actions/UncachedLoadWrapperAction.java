package edu.ualberta.med.biobank.common.wrappers.actions;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Perform
 * 
 * @author jferland
 * 
 * @param <E>
 */
// TODO: switch to decorating rather than tons of derived classes :-(
public abstract class UncachedLoadWrapperAction<E> extends
    UncachedWrapperAction<E> {
    private static final long serialVersionUID = 1L;

    protected UncachedLoadWrapperAction(ModelWrapper<E> wrapper) {
        super(wrapper);
    }

    @Override
    public final void doWithoutCache(Session session)
        throws BiobankSessionException {
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
