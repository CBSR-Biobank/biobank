package edu.ualberta.med.biobank.common.action.util;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.wrappers.Property;

public class PreCheck<E> {
    private final Session session;
    private final E model;

    public PreCheck(Session session, E model) {
        this.session = session;
        this.model = model;
    }

    public <T> void notNull(Property<T, ? super E> property)
        throws ActionException {

        T value = HqlInterceptor.get(session, model, property, 1);

        if (value == null) {
            throw new ActionException("TODO");
        }

    }

    @SuppressWarnings("unused")
    public <T> void unique(Property<T, ? super E> property)
        throws ActionException {

    }
}
