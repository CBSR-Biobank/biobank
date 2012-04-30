package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.mvp.presenter.impl.ActivityStatusComboPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.SelectedValueField;
import edu.ualberta.med.biobank.mvp.view.item.ComboBox;

// TODO: consider just making this a widget, no Presenter?
public class ActivityStatusComboView implements
    ActivityStatusComboPresenter.View {
    private final ComboBox<ActivityStatus> activityStatus = new ComboBox<ActivityStatus>();

    @Override
    public void create(Composite parent) {
        activityStatus.setComboViewer(new ComboViewer(parent));
    }

    @Override
    public SelectedValueField<ActivityStatus> getActivityStatus() {
        return activityStatus;
    }
}
