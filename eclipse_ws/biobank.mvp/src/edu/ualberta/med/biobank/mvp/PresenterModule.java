package edu.ualberta.med.biobank.mvp;

import com.google.inject.AbstractModule;

import edu.ualberta.med.biobank.mvp.action.StaleSafeDispatcher;
import edu.ualberta.med.biobank.mvp.presenter.impl.ActivityStatusComboPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEntryPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteViewPresenter;

public class PresenterModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(StaleSafeDispatcher.class);

        bind(ActivityStatusComboPresenter.class);
        bind(AddressEntryPresenter.class);
        bind(FormManagerPresenter.class);
        bind(SiteEntryPresenter.class);
        bind(SiteViewPresenter.class);
    }
}
