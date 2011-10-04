package edu.ualberta.med.biobank.event;

public interface ValueChangeHandler<T> {
	public void onValueChange(ValueChangeEvent<T> event);
}
