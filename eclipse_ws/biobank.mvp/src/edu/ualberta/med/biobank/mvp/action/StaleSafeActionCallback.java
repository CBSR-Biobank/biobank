package edu.ualberta.med.biobank.mvp.action;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.mvp.action.StaleSafeDispatcher.AsyncContext.Call;

public class StaleSafeActionCallback<T extends ActionResult>
    implements ActionCallback<T> {
    private final Call call;
    private final ActionCallback<T> callback;

    public StaleSafeActionCallback(Call call, ActionCallback<T> callback) {
        this.call = call;
        this.callback = callback;
    }

    @Override
    public void onFailure(Throwable caught) {
        if (call.finish()) {
            callback.onFailure(caught);
        }
    }

    @Override
    public void onSuccess(T result) {
        if (call.finish()) {
            callback.onSuccess(result);
        }
    }
}
