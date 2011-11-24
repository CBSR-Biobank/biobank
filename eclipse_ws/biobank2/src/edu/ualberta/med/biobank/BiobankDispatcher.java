package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class BiobankDispatcher implements Dispatcher {
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
}
