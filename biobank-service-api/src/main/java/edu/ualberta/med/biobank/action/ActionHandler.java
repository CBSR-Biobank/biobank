package edu.ualberta.med.biobank.action;

import java.util.List;

import edu.ualberta.med.biobank.i18n.ActionException;

/**
 * Implementors will support the execution {@link #getActionType()} and return
 * the specified {@link ActionResult}.
 * 
 * @author Jonathan Ferland
 * 
 * @param <A> type of {@link Action}.
 * @param <R> type of result ({@link ActionResult}) returned by the
 *            {@link Action}.
 */
public interface ActionHandler<A extends Action<R>, R extends ActionResult> {
    /**
     * @return the type of {@link Action} handled.
     */
    Class<A> getActionType();

    /**
     * Execute the given {@link Action} and include an {@link ActionExecutor} so
     * that additional {@link Action}-s may be executed.
     * 
     * @param action
     * @param executor
     * @return The {@link ActionResult}.
     * @throws ActionException if there is a problem executing the given
     *             {@link Action}.
     */
    R run(A action, ActionExecutor executor)
        throws ActionException;

    void rollback(A action, R result, ActionExecutor executor)
        throws ActionException;

    boolean allowed(A action);
}