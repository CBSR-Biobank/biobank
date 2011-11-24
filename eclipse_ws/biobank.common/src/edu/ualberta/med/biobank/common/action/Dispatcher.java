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
}
