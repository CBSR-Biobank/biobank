package edu.ualberta.med.biobank.mvp.user.ui.impl;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class DelegatingHasValue<T> implements HasValue<T> {
    private final DelegateValueChangeHandler delegateValueChangeHandler =
        new DelegateValueChangeHandler();
    private final HandlerManager handlerManager = new HandlerManager(this);
    private T defaultValue = null;
    private HasValue<T> delegate;
    private HandlerRegistration registration;

    public DelegatingHasValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public DelegatingHasValue(HasValue<T> delegate) {
        setDelegate(delegate);
    }

    public void setDelegate(HasValue<T> delegate) {
        if (registration != null) {
            registration.removeHandler();
            registration = null;
        }

        this.delegate = delegate;

        if (delegate != null) {
            registration =
                delegate.addValueChangeHandler(delegateValueChangeHandler);
        }

        fireValueChanged();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<T> handler) {
        return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public T getValue() {
        return delegate != null ? delegate.getValue() : defaultValue;
    }

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        if (delegate == null) {
            throw new IllegalStateException("delegate is null");
        }

        delegate.setValue(value, fireEvents);

        if (fireEvents) {
            fireValueChanged();
        }
    }

    private void fireValueChanged() {
        ValueChangeEvent.fire(this, getValue());
    }

    private class DelegateValueChangeHandler implements ValueChangeHandler<T> {
        public void onValueChange(ValueChangeEvent<T> event) {
            fireValueChanged();
        }
    }
}
