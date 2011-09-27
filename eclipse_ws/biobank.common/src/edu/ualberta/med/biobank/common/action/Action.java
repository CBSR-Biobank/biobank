package edu.ualberta.med.biobank.common.action;

import java.io.Serializable;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.User;

public interface Action<T> extends Serializable {
    public boolean isAllowed(User user, Session session);

    public T doAction(Session session) throws ActionException;
}
