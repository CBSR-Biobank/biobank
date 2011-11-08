package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.EventHandler;

public interface ExceptionHandler extends EventHandler {
    public void onException(ExceptionEvent event);
}
