package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.EventHandler;

public interface SiteChangedHandler extends EventHandler {
    public void onSiteChanged(SiteChangedEvent event);
}
