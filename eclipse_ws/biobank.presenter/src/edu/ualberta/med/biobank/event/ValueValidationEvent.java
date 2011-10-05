package edu.ualberta.med.biobank.event;

public interface ValueValidationEvent<T> extends Event<ValueValidationHandler<T>> {
	public T getValue();
	
	public boolean isValid();
	
	public String getErrorMessage();
}
