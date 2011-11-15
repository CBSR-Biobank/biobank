package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.event.model.site.SiteCreateEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteCreateHandler;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteEditEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteEditHandler;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteViewEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteViewHandler;
import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter.View;
import edu.ualberta.med.biobank.mvp.view.IFormView;
import edu.ualberta.med.biobank.mvp.view.IView;

public class FormManagerPresenter extends AbstractPresenter<View> {
    private Provider<SiteEntryPresenter> siteEntryPresenterProvider;
    private Provider<SiteViewPresenter> siteViewPresenterProvider;

    public interface View extends IView {
        void openForm(IFormView view);
    }

    @Inject
    public FormManagerPresenter(View view, EventBus eventBus,
        Provider<SiteEntryPresenter> siteEntryPresenterProvider,
        Provider<SiteViewPresenter> siteViewPresenterProvider) {
        super(view, eventBus);
        this.siteEntryPresenterProvider = siteEntryPresenterProvider;
        this.siteViewPresenterProvider = siteViewPresenterProvider;
    }

    @Override
    protected void onBind() {
        registerHandler(eventBus.addHandler(SiteEditEvent.getType(),
            new SiteEditHandler() {
                @Override
                public void onSiteEdit(SiteEditEvent event) {
                    doSiteEdit(event.getSiteId());
                }
            }));

        registerHandler(eventBus.addHandler(SiteCreateEvent.getType(),
            new SiteCreateHandler() {
                @Override
                public void onSiteCreate(SiteCreateEvent event) {
                    doSiteCreate();
                }
            }));

        registerHandler(eventBus.addHandler(SiteViewEvent.getType(),
            new SiteViewHandler() {
                @Override
                public void onSiteView(SiteViewEvent event) {
                    doSiteView(event.getSiteId());
                }
            }));
    }

    @Override
    protected void onUnbind() {
    }

    private void doSiteEdit(Integer siteId) {
        SiteEntryPresenter presenter = siteEntryPresenterProvider.get();
        presenter.bind();

        if (presenter.editSite(siteId)) {
            view.openForm(presenter.getView());
        }
    }

    private void doSiteCreate() {
        SiteEntryPresenter presenter = siteEntryPresenterProvider.get();
        presenter.bind();
        presenter.createSite();

        view.openForm(presenter.getView());
    }

    private void doSiteView(Integer siteId) {
        SiteViewPresenter presenter = siteViewPresenterProvider.get();
        presenter.bind();

        if (presenter.viewSite(siteId)) {
            view.openForm(presenter.getView());
        }
    }
}
