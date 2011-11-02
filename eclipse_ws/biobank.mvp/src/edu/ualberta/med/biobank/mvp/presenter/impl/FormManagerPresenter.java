package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.Arrays;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteCreateEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteCreateHandler;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteEditEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.SiteEditHandler;
import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter.View;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;
import edu.ualberta.med.biobank.mvp.view.IFormView;
import edu.ualberta.med.biobank.mvp.view.IView;

public class FormManagerPresenter extends AbstractPresenter<View> {
    private Provider<SiteEntryPresenter> siteEntryPresenterProvider;

    public interface View extends IView {
        /**
         * 
         * @param object determines uniqueness
         * @param view
         */
        void openForm(Object object, IFormView view);
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
        SiteEntryPresenter siteEntryPres = siteEntryPresenterProvider.get();
        siteEntryPres.bind();
        IEntryFormView formView = siteEntryPres.editSite(siteId);

        // TODO: think about unique object
        // TODO: the view could implement a method that explains how it's
        // unique?
        view.openForm(Arrays.asList(Site.class, siteId), formView);
    }

    private void doSiteCreate() {
        SiteEntryPresenter siteEntryPres = siteEntryPresenterProvider.get();
        siteEntryPres.bind();
        IEntryFormView formView = siteEntryPres.createSite();

        // TODO: think about unique object
        view.openForm(new Object(), formView);
    }
}
