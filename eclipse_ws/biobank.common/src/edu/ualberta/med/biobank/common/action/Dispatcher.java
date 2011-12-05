package edu.ualberta.med.biobank.common.action;

public interface Dispatcher {
    public <T extends ActionResult> T exec(Action<T> action);

    /**
     * 
     * @param action
     * @param cb
     * @return {@code true} if successful, otherwise {@code false}.
     */
    public <T extends ActionResult> boolean exec(Action<T> action,
        ActionCallback<T> cb);

    /**
     * Asynchronously execute the {@link Action} and run the
     * {@link ActionCallback} when the server returns a result.
     * 
     * @param action
     * @param cb
     */
    public <T extends ActionResult> void asyncExec(Action<T> action,
        ActionCallback<T> cb);
}
