package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.EventHandler;

public interface SiteViewHandler extends EventHandler {
    public void onSiteView(SiteViewEvent event);
}
