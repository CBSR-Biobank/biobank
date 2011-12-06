package edu.ualberta.med.biobank.mvp.action;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.ActionResult;

public class StaleSafeActionCallback<T extends ActionResult>
    implements ActionCallback<T> {
    private final ActionCallback<T> callback;

    public StaleSafeActionCallback(ActionCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onFailure(Throwable caught) {
    }

    @Override
    public void onSuccess(T result) {
    }
}
