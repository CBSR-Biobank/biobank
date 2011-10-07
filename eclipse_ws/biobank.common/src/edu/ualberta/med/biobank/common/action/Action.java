package edu.ualberta.med.biobank.common.action;

import java.io.Serializable;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.User;

public interface Action<T extends Serializable> extends Serializable {
    public boolean isAllowed(User user, Session session) throws ActionException;

    public T run(User user, Session session) throws ActionException;
}
