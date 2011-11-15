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
// TODO: make the returned object implement ActionResponse
// TODO: use a "Context" object instead of User and Session? SCRATCH THAT. It
// makes more sense to have ActionInput mapped to ActionResult and a handler
// (the handler has a Session and a User, and can populate the response however
// it wants, using whatever it wants). This will make them easier to mock as
// well.
public interface Action<T extends Serializable> extends NotAProxy, Serializable {
    public boolean isAllowed(User user, Session session) throws ActionException;

    public T run(User user, Session session) throws ActionException;
}
