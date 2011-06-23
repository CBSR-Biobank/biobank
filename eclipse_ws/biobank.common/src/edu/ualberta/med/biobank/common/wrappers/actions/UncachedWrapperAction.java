package edu.ualberta.med.biobank.common.wrappers.actions;

import org.hibernate.CacheMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * A {@link WrapperAction} that performs an action outside of (ignoring) the
 * {@link Hibernate} cache. See {@link CacheMode}.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public abstract class UncachedWrapperAction<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;

    protected UncachedWrapperAction(ModelWrapper<E> wrapper) {
        super(wrapper);
    }

    @Override
    public final Object doAction(Session session)
        throws BiobankSessionException {

        CacheMode oldCacheMode = session.getCacheMode();

        try {
            session.setCacheMode(CacheMode.IGNORE);
            doWithoutCache(session);
        } finally {
            session.setCacheMode(oldCacheMode);
        }

        return null;
    }

    public abstract void doWithoutCache(Session session)
        throws BiobankSessionException;
}
