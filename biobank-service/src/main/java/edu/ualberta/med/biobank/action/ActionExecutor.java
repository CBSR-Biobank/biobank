package edu.ualberta.med.biobank.action;

import edu.ualberta.med.biobank.action.exception.ActionException;

public interface ActionExecutor {
    <A extends Action2p0<R>, R extends ActionResult> R run(A action)
        throws ActionException;
}
