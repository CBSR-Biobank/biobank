package edu.ualberta.med.biobank.mvp.event.model;

import com.google.gwt.event.shared.EventHandler;

public interface SiteEditHandler extends EventHandler {
    public void onSiteEdit(SiteEditEvent event);
}
