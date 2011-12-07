package edu.ualberta.med.biobank;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.forms.StudyEntryFormView;
import edu.ualberta.med.biobank.mvp.PresenterModule;
import edu.ualberta.med.biobank.mvp.presenter.impl.ActivityStatusComboPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEntryPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteViewPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.SpecimenLinkPresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.StudyEntryPresenter;
import edu.ualberta.med.biobank.mvp.view.ActivityStatusComboView;
import edu.ualberta.med.biobank.mvp.view.AddressEntryView;
import edu.ualberta.med.biobank.mvp.view.FormManagerView;
import edu.ualberta.med.biobank.mvp.view.form.SiteEntryFormView;
import edu.ualberta.med.biobank.mvp.view.form.SiteViewFormView;
import edu.ualberta.med.biobank.mvp.view.form.SpecimenLinkView;

public class BiobankModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new PresenterModule());

        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(Dispatcher.class).to(BiobankDispatcher.class).in(Singleton.class);

        bind(AddressEntryPresenter.View.class).to(AddressEntryView.class);

        bind(SiteEntryPresenter.View.class).to(SiteEntryFormView.class);
        bind(SiteViewPresenter.View.class).to(SiteViewFormView.class);

        bind(ActivityStatusComboPresenter.View.class).to(
            ActivityStatusComboView.class);
        bind(StudyEntryPresenter.View.class).to(StudyEntryFormView.class);

        bind(FormManagerPresenter.View.class).to(FormManagerView.class);

        bind(SpecimenLinkPresenter.View.class).to(SpecimenLinkView.class);
    }
}
