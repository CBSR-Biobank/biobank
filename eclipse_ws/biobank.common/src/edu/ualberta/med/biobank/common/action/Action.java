package edu.ualberta.med.biobank.common.action;

import java.io.Serializable;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.User;

/**
 * Implementations of this interface should follow the template
 * "{noun}{verb}{noun}..Action," for example, SiteGetInfoAction and
 * SiteGetTopContainersInfoAction, NOT GetSiteInfoAction.
 * 
 * @author jferland
 * 
 * @param <T>
 */
public interface Action<T extends Serializable> extends NotAProxy, Serializable {
    public boolean isAllowed(User user, Session session) throws ActionException;

    public T run(User user, Session session) throws ActionException;
}
