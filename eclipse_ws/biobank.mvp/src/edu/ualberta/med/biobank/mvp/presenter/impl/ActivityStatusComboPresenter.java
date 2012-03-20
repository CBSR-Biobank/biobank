package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.mvp.presenter.HasState;
import edu.ualberta.med.biobank.mvp.presenter.IStatefulPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.ActivityStatusComboPresenter.View;
import edu.ualberta.med.biobank.mvp.presenter.state.ModelState;
import edu.ualberta.med.biobank.mvp.user.ui.SelectedValueField;
import edu.ualberta.med.biobank.mvp.util.Converter;
import edu.ualberta.med.biobank.mvp.view.IView;

public class ActivityStatusComboPresenter extends AbstractPresenter<View>
    implements IStatefulPresenter {
    private final static OptionLabeller LABELLER = new OptionLabeller();
    private final ModelState state = new ModelState();

    @SuppressWarnings("unused")
    private final Dispatcher dispatcher;

    public interface View extends IView {
        SelectedValueField<ActivityStatus> getActivityStatus();
    }

    @Inject
    public ActivityStatusComboPresenter(View view, EventBus eventBus,
        Dispatcher dispatcher) {
        super(view, eventBus);
        this.dispatcher = dispatcher;

        view.getActivityStatus().setOptionLabeller(LABELLER);
    }

    @Override
    protected void onBind() {
        state.add(view.getActivityStatus());
    }

    @Override
    protected void onUnbind() {
        state.dispose();
    }

    @Override
    public HasState getState() {
        return state;
    }

    public ActivityStatus getActivityStatus() {
        return view.getActivityStatus().getValue();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        loadOptions();

        view.getActivityStatus().setValue(activityStatus);
    }

    private void loadOptions() {
        view.getActivityStatus()
            .setOptions(ActivityStatus.valuesList());
    }

    private static class OptionLabeller implements
        Converter<ActivityStatus, String> {
        @Override
        public String convert(ActivityStatus activityStatus) {
            return activityStatus.getName();
        }
    }
}
