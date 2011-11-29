package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.ArrayList;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.common.action.MapResult;
import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusGetAllAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.mvp.presenter.impl.ActivityStatusComboPresenter.View;
import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValueField;
import edu.ualberta.med.biobank.mvp.util.Converter;
import edu.ualberta.med.biobank.mvp.view.IView;

public class ActivityStatusComboPresenter extends AbstractPresenter<View> {
    private final static OptionLabeller labeller = new OptionLabeller();
    private final Dispatcher dispatcher;

    public interface View extends IView {
        HasSelectedValueField<ActivityStatus> getActivityStatus();
    }

    @Inject
    public ActivityStatusComboPresenter(View view, EventBus eventBus,
        Dispatcher dispatcher) {
        super(view, eventBus);
        this.dispatcher = dispatcher;
    }

    @Override
    protected void onBind() {
    }

    @Override
    protected void onUnbind() {
    }

    public ActivityStatus getActivityStatus() {
        return view.getActivityStatus().getValue();
    }

    public Integer getActivityStatusId() {
        return getActivityStatus() != null ? getActivityStatus().getId() : null;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        loadOptions();
        view.getActivityStatus().setValue(activityStatus);
    }

    private void loadOptions() {
        dispatcher.exec(new ActivityStatusGetAllAction(),
            new ActionCallback<MapResult<Integer, ActivityStatus>>() {
                @Override
                public void onFailure(Throwable caught) {
                    // TODO: need to do something if cannot get information,
                    // like throwing an ExceptionEvent. Parent Presenter may
                    // need to tear down/ fail if this fails.
                    System.out.println("FAIL!");
                    caught.printStackTrace();
                }

                @Override
                public void onSuccess(MapResult<Integer, ActivityStatus> result) {
                    view.getActivityStatus()
                        .setOptions(
                            new ArrayList<ActivityStatus>(result.getMap()
                                .values()));
                    view.getActivityStatus().setOptionLabeller(labeller);
                }
            });
    }

    private static class OptionLabeller implements
        Converter<ActivityStatus, String> {
        @Override
        public String convert(ActivityStatus activityStatus) {
            return activityStatus.getName();
        }
    }
}
