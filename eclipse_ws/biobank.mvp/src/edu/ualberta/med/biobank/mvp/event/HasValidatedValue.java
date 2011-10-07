package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.user.client.ui.HasValue;

public interface HasValidatedValue<T> extends HasValue<T> {
	public ValueValidationHandler<T> getValueValidationHandler();
}
