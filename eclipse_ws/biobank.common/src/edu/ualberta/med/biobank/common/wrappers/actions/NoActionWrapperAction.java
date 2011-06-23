package edu.ualberta.med.biobank.common.wrappers.actions;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Do nothing but return null.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public class NoActionWrapperAction<E> extends BiobankWrapperAction<E> {
    private static final long serialVersionUID = 1L;

    public NoActionWrapperAction(ModelWrapper<E> wrapper) {
        super(wrapper);
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        return null;
    }
}