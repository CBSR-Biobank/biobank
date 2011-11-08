package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.event.model.site.SiteCreateEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteCreateHandler;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteEditEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteEditHandler;
import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter.View;
import edu.ualberta.med.biobank.mvp.view.IFormView;
import edu.ualberta.med.biobank.mvp.view.IView;

public class FormManagerPresenter extends AbstractPresenter<View> {
    private Provider<SiteEntryPresenter> siteEntryPresenterProvider;

    public interface View extends IView {
        void openForm(IFormView view);
    }

    @Inject
    public FormManagerPresenter(View view, EventBus eventBus,
        Provider<SiteEntryPresenter> siteEntryPresenterProvider) {
        super(view, eventBus);
        this.siteEntryPresenterProvider = siteEntryPresenterProvider;
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
}
