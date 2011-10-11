package edu.ualberta.med.biobank.mvp.presenter.impl;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.mvp.presenter.impl.SelectActivityStatusPresenter.Display;
import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValue;
import edu.ualberta.med.biobank.mvp.view.View;

public class SelectActivityStatusPresenter extends BasePresenter<Display> {
    // TODO: Display should be passed from enclosing class.
    public interface Display extends View {
        HasSelectedValue<ActivityStatus> getActivityStatus();
    }

    @Override
    protected void onBind() {
        // TODO: get ActivityStatus-es from the database, setOptions, and
        // setOptionLabeler.
    }

    @Override
    protected void onUnbind() {
    }
}
