package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.mvp.presenter.impl.ActivityStatusComboPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValueField;
import edu.ualberta.med.biobank.mvp.view.item.ComboItem;

// TODO: consider just making this a widget, no Presenter?
public class ActivityStatusComboView implements
    ActivityStatusComboPresenter.View {
    private final ComboItem<ActivityStatus> activityStatus = new ComboItem<ActivityStatus>();

    @Override
    public void create(Composite parent) {
        activityStatus.setComboViewer(new ComboViewer(parent));
    }

    @Override
    public HasSelectedValueField<ActivityStatus> getActivityStatus() {
        return activityStatus;
    }
}
