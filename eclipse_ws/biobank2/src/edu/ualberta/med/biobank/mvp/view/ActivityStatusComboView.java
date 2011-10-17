package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.mvp.presenter.impl.ActivityStatusComboPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValue;

// TODO: consider just making this a widget, no Presenter?
public class ActivityStatusComboView implements
    ActivityStatusComboPresenter.View {

    @Override
    public void create(Composite parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public HasSelectedValue<ActivityStatus> getActivityStatus() {
        // TODO Auto-generated method stub
        return null;
    }
}
