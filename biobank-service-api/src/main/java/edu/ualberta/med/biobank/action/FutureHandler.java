package edu.ualberta.med.biobank.action;

import edu.ualberta.med.biobank.i18n.ActionException;

public class FutureHandler<A extends Action<R>, R extends ActionResult>
    implements ActionHandler<FutureAction<A, R>, FutureResult<R>> {

    @Override
    public Class<FutureAction<A, R>> getActionType() {
        return FutureAction.class;
    }

    @Override
    public FutureResult<R> run(FutureAction<A, R> action,
        ActionExecutor executor) throws ActionException {
        A innerAction = action.getAction();
        R innerResult = executor.run(innerAction);
        action.getResult().setResult(innerResult);
        return action.getResult();
    }

    @Override
    public void rollback(FutureAction<A, R> action, FutureResult<R> result,
        ActionExecutor executor) throws ActionException {
        executor.rollback(action, result);
    }

    @Override
    public boolean allowed(FutureAction<A, R> action) {
        return true;
    }
}
