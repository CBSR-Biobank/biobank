package edu.ualberta.med.biobank.common.wrappers.checks;

import org.hibernate.CacheMode;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankWrapperAction;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

// TODO: write description
public abstract class BiobankWrapperCheck<E> extends BiobankWrapperAction<E> {
    private static final long serialVersionUID = 1L;

    protected BiobankWrapperCheck(ModelWrapper<E> wrapper) {
        super(wrapper);
    }

    @Override
    public final Object doAction(Session session)
        throws BiobankSessionException {

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
