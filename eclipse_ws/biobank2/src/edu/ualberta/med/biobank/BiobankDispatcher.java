package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.mvp.action.AbstractDispatcher;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class BiobankDispatcher extends AbstractDispatcher {
    @Override
    protected <T extends ActionResult> T doExec(Action<T> action)
        throws Exception {
        BiobankApplicationService service = SessionManager.getAppService();
        return service.doAction(action);
    }
}
