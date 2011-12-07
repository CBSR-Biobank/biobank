package edu.ualberta.med.biobank.mvp.action;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.Dispatcher;

public abstract class AbstractDispatcher implements Dispatcher {
    // TODO: need to shut this down? expose method to do so?
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @Override
    public <T extends ActionResult> T exec(Action<T> action) {
        T result = null;

        try {
            result = doExec(action);
        } catch (Exception e) {
            // TODO: handle this better by (1) declaring thrown exception(s)?
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public <T extends ActionResult> Future<T> asyncExec(final Action<T> action,
        final ActionCallback<T> callback) {

        Future<T> future = pool.submit(new Callable<T>() {
            @Override
            public T call() {
                T result = null;

                try {
                    result = doExec(action);
                    callback.onSuccess(result);
                } catch (Throwable caught) {
                    callback.onFailure(caught);
                }

                return result;
            }
        });

        // TODO: if future.cancel() is called, then will onFailure() be called?
        // May need to listen for an interrupt then call onFailure().

        return future;
    }

    protected abstract <T extends ActionResult> T doExec(Action<T> action)
        throws Exception;
}
