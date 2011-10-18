package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.EventHandler;

public interface ValidationHandler extends EventHandler {
    public void onValidate(ValidationEvent event);
}
