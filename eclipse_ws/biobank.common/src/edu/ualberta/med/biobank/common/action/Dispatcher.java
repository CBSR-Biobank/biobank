package edu.ualberta.med.biobank.common.action;

import java.util.concurrent.Future;

public interface Dispatcher {
    /**
     * Synchronously executes an {@link action} and returns the
     * {@link ActionResult}.
     * 
     * @param action
     * @return
     */
    public <T extends ActionResult> T exec(Action<T> action);

    /**
     * Asynchronously execute the {@link Action} and run the
     * {@link ActionCallback} when the server returns a result.
     * <p>
     * Returns a {@link Future} object that can be used to wait for the result,
     * cancel the action, etc.
     * 
     * @param action
     * @param callback
     * @return the future... dun dun dun...
     */
    public <T extends ActionResult> Future<T> asyncExec(Action<T> action,
        ActionCallback<T> callback);
}
