package edu.ualberta.med.biobank.mvp.event.presenter.site;

import com.google.gwt.event.shared.EventHandler;

public interface SiteEntryPresenterShownHandler extends EventHandler {
    public void onSiteEntryPresenterShown(SiteEntryPresenterShownEvent event);
}
