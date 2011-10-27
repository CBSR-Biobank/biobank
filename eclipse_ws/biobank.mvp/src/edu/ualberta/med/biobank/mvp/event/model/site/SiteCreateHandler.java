package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.EventHandler;

public interface SiteCreateHandler extends EventHandler {
    public void onSiteCreate(SiteCreateEvent event);
}
