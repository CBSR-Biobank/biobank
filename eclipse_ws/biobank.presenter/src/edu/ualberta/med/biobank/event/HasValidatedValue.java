package edu.ualberta.med.biobank.event;

public interface HasValidatedValue<T> extends HasValue<T> {
	public ValueValidationHandler<T> getValueValidationHandler();
}
