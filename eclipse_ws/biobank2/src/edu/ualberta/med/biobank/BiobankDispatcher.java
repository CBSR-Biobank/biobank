package edu.ualberta.med.biobank;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class BiobankDispatcher implements Dispatcher {
    // TODO: need to shut this down.
    private final ExecutorService executorService = Executors
        .newFixedThreadPool(6);

    @Override
    public <T extends ActionResult> T exec(Action<T> action) {
        BiobankApplicationService service = SessionManager.getAppService();
        T result = null;
        try {
            result = service.doAction(action);
        } catch (Exception e) {
            // TODO: handle this better by (1) declaring thrown exception(s)?
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public <T extends ActionResult> boolean exec(Action<T> action,
        ActionCallback<T> callback) {
        boolean success = false;

        try {
            BiobankApplicationService service = SessionManager.getAppService();
            T result = service.doAction(action);
            success = true;
            callback.onSuccess(result);
        } catch (Throwable caught) {
            callback.onFailure(caught);
        }

        return success;
    }

    @Override
    public <T extends ActionResult> void asyncExec(final Action<T> action,
        final ActionCallback<T> callback) {
        final BiobankApplicationService service =
            SessionManager.getAppService();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    T result = service.doAction(action);
                    callback.onSuccess(result);
                } catch (Throwable caught) {
                    callback.onFailure(caught);
                }
            }
        });
    }
}
