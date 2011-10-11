package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.ArrayList;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.activityStatus.GetAllActivityStatusesAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.mvp.presenter.impl.SelectActivityStatusPresenter.Display;
import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValue;
import edu.ualberta.med.biobank.mvp.util.Converter;
import edu.ualberta.med.biobank.mvp.view.View;

public class SelectActivityStatusPresenter extends BasePresenter<Display> {
    private final static OptionLabeller labeller = new OptionLabeller();

    public interface Display extends View {
        HasSelectedValue<ActivityStatus> getActivityStatus();
    }

    @Override
    protected void onBind() {
        dispatcher.exec(new GetAllActivityStatusesAction(),
            new ActionCallback<ArrayList<ActivityStatus>>() {
                @Override
                public void onFailure(Throwable caught) {
                    // TODO: need to do something if cannot get information,
                    // like throwing an ExceptionEvent. Parent Presenter may
                    // need to tear down/ fail if this fails.
                }

                @Override
                public void onSuccess(ArrayList<ActivityStatus> result) {
                    display.getActivityStatus().setOptions(result);
                    display.getActivityStatus().setOptionLabeller(labeller);
                }
            });
    }

    @Override
    protected void onUnbind() {
    }

    private static class OptionLabeller implements
        Converter<ActivityStatus, String> {
        @Override
        public String convert(ActivityStatus activityStatus) {
            return activityStatus.getName();
        }
    }
}
