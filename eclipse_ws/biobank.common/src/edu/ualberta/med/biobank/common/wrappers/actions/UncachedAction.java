package edu.ualberta.med.biobank.common.wrappers.actions;

import org.hibernate.CacheMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Decorates a {@link BiobankSessionAction}. Performs the given
 * {@link BiobankSessionAction} outside of (ignoring) the {@link Hibernate}
 * cache. See {@link CacheMode}.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public final class UncachedAction<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;

    private final BiobankSessionAction action;

    /**
     * 
     * @param action the action to perform outside of the cache
     */
    public UncachedAction(ModelWrapper<E> wrapper, BiobankSessionAction action) {
        super(wrapper);
        this.action = action;
    }

    @Override
    public final Object doAction(Session session)
        throws BiobankSessionException {

        CacheMode oldCacheMode = session.getCacheMode();

        try {
            session.setCacheMode(CacheMode.IGNORE);
            action.doAction(session);
        } finally {
            session.setCacheMode(oldCacheMode);
        }

        return null;
    }
}
