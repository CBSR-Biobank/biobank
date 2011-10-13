package edu.ualberta.med.biobank.mvp.event.model.site;

import com.google.gwt.event.shared.EventHandler;

public interface EditSiteHandler extends EventHandler {
    public void onEditSite(EditSiteEvent event);
}
