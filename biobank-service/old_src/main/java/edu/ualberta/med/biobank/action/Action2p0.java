package edu.ualberta.med.biobank.action;

import java.io.Serializable;

import edu.ualberta.med.biobank.model.util.NotAProxy;

/**
 * Represents a command to be executed by an {@link ActionExecutor} that returns
 * the parameterised type on success.
 * <p>
 * Implementations of this interface should follow the template
 * "{noun}{verb}{noun}..Action," for example, SiteGetInfoAction and
 * SiteGetTopContainersInfoAction, NOT GetSiteInfoAction.
 * 
 * @author Jonathan Ferland
 * 
 * @param <R> the returned type.
 */
public interface Action2p0<R extends ActionResult>
    extends NotAProxy, Serializable {
}
