package edu.ualberta.med.biobank.common.wrappers.checks;

import org.hibernate.CacheMode;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * The {@link Session} {@link CacheMode} is set to {@link CacheMode.IGNORE}
 * while the {@code doCheck()} method is called, then reset to the original
 * value. This is based on the idea that checks should not affect the cache.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public abstract class WrapperCheck<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;

    protected WrapperCheck(ModelWrapper<E> wrapper) {
        super(wrapper);
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        CacheMode oldCacheMode = session.getCacheMode();

        try {
            session.setCacheMode(CacheMode.IGNORE);
            doCheck(session);
        } finally {
            session.setCacheMode(oldCacheMode);
        }

        return null;
    }

    public abstract void doCheck(Session session)
        throws BiobankSessionException;
}
