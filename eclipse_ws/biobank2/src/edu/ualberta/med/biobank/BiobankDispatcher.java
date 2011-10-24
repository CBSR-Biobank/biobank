package edu.ualberta.med.biobank;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class BiobankDispatcher implements Dispatcher {
    @Override
    public <T extends Serializable> T exec(Action<T> action) {
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
    public <T extends Serializable> void exec(Action<T> action,
        ActionCallback<T> callback) {
        try {
            BiobankApplicationService service = SessionManager.getAppService();
            T result = service.doAction(action);
            callback.onSuccess(result);
        } catch (Throwable caught) {
            callback.onFailure(caught);
        }
    }
}
