package edu.ualberta.med.biobank.mvp.event.model;

import com.google.gwt.event.shared.EventHandler;

public interface SiteSavedHandler extends EventHandler {
    public void onSiteSaved(SiteSavedEvent event);
}
