package edu.ualberta.med.biobank.mvp.view.item;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.event.HasValidatedValue;
import edu.ualberta.med.biobank.mvp.event.ValueValidationHandler;

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

    @Override
    public void fireEvent(GwtEvent<?> event) {
        // TODO Auto-generated method stub

    }
}
