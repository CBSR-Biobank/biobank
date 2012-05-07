package edu.ualberta.med.biobank.common.action;

import edu.ualberta.med.biobank.common.action.exception.ActionException;

/**
 * Implementors will support the execution {@link #getActionType()} and return
 * the specified {@link ActionResult}.
 * 
 * @author Jonathan Ferland
 * 
 * @param <A> type of {@link Action2p0}.
 * @param <R> type of result returned by the {@link Action2p0}.
 */
public interface ActionHandler<A extends Action2p0<R>, R extends ActionResult> {
    /**
     * @return the type of {@link Action2p0} handled.
     */
    Class<A> getActionType();

    /**
     * Execute the given {@link Action2p0} and include an {@link ActionExecutor}
     * so that additional {@link Action2p0}-s may be executed.
     * 
     * @param action
     * @param executor
     * @return The {@link ActionResult}.
     * @throws ActionException if there is a problem executing the given
     *             {@link Action2p0}.
     */
    R run(A action, ActionExecutor executor) throws ActionException;
}