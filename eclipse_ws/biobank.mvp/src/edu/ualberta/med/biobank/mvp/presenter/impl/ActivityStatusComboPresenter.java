package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.activityStatus.GetAllActivityStatusesAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.mvp.presenter.impl.ActivityStatusComboPresenter.View;
import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValue;
import edu.ualberta.med.biobank.mvp.util.Converter;
import edu.ualberta.med.biobank.mvp.view.BaseView;

public class ActivityStatusComboPresenter extends BasePresenter<View> {
    private final static OptionLabeller labeller = new OptionLabeller();
    private final Dispatcher dispatcher;

    public interface View extends BaseView {
        HasSelectedValue<ActivityStatus> getActivityStatus();
    }

    @Inject
    public ActivityStatusComboPresenter(View view, EventBus eventBus,
        Dispatcher dispatcher) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
    }

    public HasValue<ActivityStatus> getActivityStatus() {
        return view.getActivityStatus();
    }

    @Override
    protected void onBind() {
        dispatcher.exec(new GetAllActivityStatusesAction(),
            new ActionCallback<HashMap<Integer, ActivityStatus>>() {
                @Override
                public void onFailure(Throwable caught) {
                    // TODO: need to do something if cannot get information,
                    // like throwing an ExceptionEvent. Parent Presenter may
                    // need to tear down/ fail if this fails.
                    System.out.println("FAIL!");
                    caught.printStackTrace();
                }

                @Override
                public void onSuccess(HashMap<Integer, ActivityStatus> result) {
                    view.getActivityStatus().setOptions(
                        new ArrayList<ActivityStatus>(result.values()));
                    view.getActivityStatus().setOptionLabeller(labeller);
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
