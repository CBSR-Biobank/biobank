package edu.ualberta.med.biobank.test;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.mvp.action.AbstractDispatcher;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class TestingDispatcher extends AbstractDispatcher {
    private final static String SERVER = System.getProperty("server",
        "http://localhost:8080") + "/biobank";

    @Override
    protected <T extends ActionResult> T doExec(Action<T> action)
        throws Exception {
        BiobankApplicationService service = ServiceConnection
            .getAppService(SERVER, "testuser", "test");
        return service.doAction(action);
    }
}
