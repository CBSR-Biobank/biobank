package edu.ualberta.med.biobank.view.item;

import edu.ualberta.med.biobank.event.HandlerRegistration;
import edu.ualberta.med.biobank.event.HasValidatedValue;
import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.event.ValueChangeHandler;
import edu.ualberta.med.biobank.event.ValueValidationHandler;

public class ValidatedItem<T> implements HasValidatedValue<T> {
    private final HasValue<T> hasValue;

    public ValidatedItem(HasValue<T> hasValue) {
        this.hasValue = hasValue;
    }

    @Override
    public T getValue() {
        return hasValue.getValue();
    }

    @Override
    public void setValue(T value) {
        hasValue.setValue(value);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        hasValue.setValue(value, fireEvents);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<T> handler) {
        return hasValue.addValueChangeHandler(handler);
    }

    @Override
    public ValueValidationHandler<T> getValueValidationHandler() {
        // TODO Auto-generated method stub
        return null;
    }
}
