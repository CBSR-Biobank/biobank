package edu.ualberta.med.biobank.mvp.presenter.impl;

import edu.ualberta.med.biobank.mvp.event.model.SiteEditEvent;
import edu.ualberta.med.biobank.mvp.event.model.SiteEditHandler;
import edu.ualberta.med.biobank.mvp.presenter.impl.EntryManagerPresenter.View;
import edu.ualberta.med.biobank.mvp.view.BaseView;
import edu.ualberta.med.biobank.mvp.view.EntryView;

public class EntryManagerPresenter extends BasePresenter<View> {
    // private final Provider<SiteEntryPresenter> siteEntryPresenterProvider;

    public interface View extends BaseView {
        /**
         * 
         * @param object
         *            determines uniqueness
         * @param view
         */
        void showView(Object object, EntryView view);
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
    }

    @Override
    protected void onUnbind() {
        // TODO Auto-generated method stub

    }

    private void doSiteEdit(Integer siteId) {
        // EntryView view = siteEntryPresenterProvider.get().editSite(siteId);
        // TODO: think about unique object
        // display.showView(new Object(), view);
    }
}
