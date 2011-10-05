package edu.ualberta.med.biobank.event;

public interface ValueValidationHandler<T> extends EventHandler {
	public void onValueValidation(ValueValidationEvent<T> event);
}
