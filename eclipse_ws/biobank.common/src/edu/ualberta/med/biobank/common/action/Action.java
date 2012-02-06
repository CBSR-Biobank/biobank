package edu.ualberta.med.biobank.common.action;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.NotAProxy;

/**
 * Implementations of this interface should follow the template
 * "{noun}{verb}{noun}..Action," for example, SiteGetInfoAction and
 * SiteGetTopContainersInfoAction, NOT GetSiteInfoAction.
 * 
 * @author jferland
 * 
 * @param <T>
 */
// TODO: use a "Context" object instead of User and Session? SCRATCH THAT. It
// makes more sense to have ActionInput mapped to ActionResult and a handler
// (the handler has a Session and a User, and can populate the response however
// it wants, using whatever it wants). This will make them easier to mock as
// well.
public interface Action<T extends ActionResult> extends NotAProxy, Serializable {
    public boolean isAllowed(ActionContext context) throws ActionException;

    public T run(ActionContext context) throws ActionException;

}
