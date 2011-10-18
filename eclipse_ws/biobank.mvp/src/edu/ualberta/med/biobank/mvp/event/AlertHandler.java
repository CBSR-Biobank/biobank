package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.EventHandler;

public interface AlertHandler extends EventHandler {
    public void onAlert(AlertEvent event);
}
