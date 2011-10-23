package edu.ualberta.med.biobank;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.mvp.PresenterModule;
import edu.ualberta.med.biobank.mvp.presenter.impl.ActivityStatusComboPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEditPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.view.ActivityStatusComboView;
import edu.ualberta.med.biobank.mvp.view.AddressEditView;
import edu.ualberta.med.biobank.mvp.view.FormManagerView;
import edu.ualberta.med.biobank.mvp.view.SiteEntryView;

public class BiobankModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new PresenterModule());

        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(Dispatcher.class).to(BiobankDispatcher.class).in(Singleton.class);

        bind(SiteEntryPresenter.View.class).to(SiteEntryView.class);
        bind(AddressEditPresenter.View.class).to(AddressEditView.class);
        bind(ActivityStatusComboPresenter.View.class).to(
            ActivityStatusComboView.class);
        bind(FormManagerPresenter.View.class).to(FormManagerView.class);
    }
}
