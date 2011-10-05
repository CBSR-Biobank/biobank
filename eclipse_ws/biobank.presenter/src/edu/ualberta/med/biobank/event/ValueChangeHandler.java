package edu.ualberta.med.biobank.event;

public interface ValueChangeHandler<T> extends EventHandler {
	public void onValueChange(ValueChangeEvent<T> event);
}
