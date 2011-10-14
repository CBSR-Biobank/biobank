package edu.ualberta.med.biobank.mvp.presenter.impl;

import java.util.Arrays;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.mvp.event.model.site.CreateSiteEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.CreateSiteHandler;
import edu.ualberta.med.biobank.mvp.event.model.site.EditSiteEvent;
import edu.ualberta.med.biobank.mvp.event.model.site.EditSiteHandler;
import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter.View;
import edu.ualberta.med.biobank.mvp.view.BaseView;
import edu.ualberta.med.biobank.mvp.view.FormView;

@ImplementedBy(FormManagerPresenter.class)
public class FormManagerPresenter extends BasePresenter<View> {
    private Provider<SiteEntryPresenter> siteEntryPresenterProvider;

    public interface View extends BaseView {
        /**
         * 
         * @param object
         *            determines uniqueness
         * @param view
         */
        void openForm(Object object, FormView view);
    }

    @Inject
    public FormManagerPresenter(View view, EventBus eventBus,
        Provider<SiteEntryPresenter> siteEntryPresenterProvider) {
        super(view, eventBus);
        this.siteEntryPresenterProvider = siteEntryPresenterProvider;
    }

    @Override
    protected void onBind() {
        registerHandler(eventBus.addHandler(EditSiteEvent.getType(),
            new EditSiteHandler() {
                @Override
                public void onEditSite(EditSiteEvent event) {
                    doSiteEdit(event.getSiteId());
                }
            }));

        registerHandler(eventBus.addHandler(CreateSiteEvent.getType(),
            new CreateSiteHandler() {
                @Override
                public void onCreateSite(CreateSiteEvent event) {
                    doSiteCreate();
                }
            }));
    }

    @Override
    protected void onUnbind() {
    }

    private void doSiteEdit(Integer siteId) {
        FormView formView = siteEntryPresenterProvider.get().editSite(siteId);

        // TODO: think about unique object
        // TODO: the view could implement a method that explains how it's
        // unique?
        view.openForm(Arrays.asList(Site.class, siteId), formView);
    }

    private void doSiteCreate() {
        FormView formView = siteEntryPresenterProvider.get().createSite();

        // TODO: think about unique object
        view.openForm(new Object(), formView);
    }
}
