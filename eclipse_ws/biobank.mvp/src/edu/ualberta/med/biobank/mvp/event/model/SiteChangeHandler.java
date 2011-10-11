package edu.ualberta.med.biobank.mvp.event.model;

import com.google.gwt.event.shared.EventHandler;

public interface SiteChangeHandler extends EventHandler {
    public void onSiteChange(SiteChangeEvent event);
}
