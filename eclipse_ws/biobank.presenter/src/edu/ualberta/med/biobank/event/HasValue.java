package edu.ualberta.med.biobank.event;

public interface HasValue<T> extends HasValueChangeHandlers<T> {
	public T getValue();
	
	/**
	 * Identical to calling {@code setValue(value, false)}.
	 * 
	 * @param value
	 */
	public void setValue(T value);
	
	public void setValue(T value, boolean fireEvents);
}
