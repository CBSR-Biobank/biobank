package edu.ualberta.med.biobank.test;

import java.io.Serializable;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class TestingDispatcher implements Dispatcher {
    @Override
    public <T extends Serializable> T exec(Action<T> action) {
        T result = null;
        try {
            BiobankApplicationService service = ServiceConnection
                .getAppService(
                    System.getProperty("server", "http://localhost:8080")
                        + "/biobank", "testuser", "test");
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
            BiobankApplicationService service = ServiceConnection
                .getAppService(
                    System.getProperty("server", "http://localhost:8080")
                        + "/biobank", "testuser", "test");
            T result = service.doAction(action);
            callback.onSuccess(result);
        } catch (Throwable caught) {
            callback.onFailure(caught);
        }
    }
}
