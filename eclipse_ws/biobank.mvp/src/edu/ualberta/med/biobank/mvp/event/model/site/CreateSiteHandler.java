package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.EventHandler;

public interface CreateSiteHandler extends EventHandler {
    public void onCreateSite(CreateSiteEvent event);
}
