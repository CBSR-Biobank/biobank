package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.EventHandler;

public interface ValueValidationHandler<T> extends EventHandler {
	public void onValueValidation(ValueValidationEvent<T> event);
}
