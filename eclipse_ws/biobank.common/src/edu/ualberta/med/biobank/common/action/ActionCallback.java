package edu.ualberta.med.biobank.common.action;

public interface ActionCallback<T extends ActionResult> {
    /**
     * Called when an command call fails to complete normally.
     * 
     * @param caught failure encountered while executing a remote procedure call
     */
    void onFailure(Throwable caught);

    /**
     * Called when an asynchronous call completes successfully.
     * 
     * @param result the return value of the remote produced call
     */
    void onSuccess(T result);
}
