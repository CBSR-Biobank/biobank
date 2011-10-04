package edu.ualberta.med.biobank.event;

public interface HasValueChangeHandlers<I> {
	HandlerRegistration addValueChangeHandler(ValueChangeHandler<I> handler);
}
