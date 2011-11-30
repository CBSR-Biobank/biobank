package edu.ualberta.med.biobank.mvp.view.item;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.event.SimpleValueChangeEvent;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;

public abstract class AbstractValueField<T> extends AbstractValidationField
    implements ValueField<T> {
    private final HandlerManager handlerManager = new HandlerManager(this);
    private T value;

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
        return value;
    }

    /**
     * Finalized to agree to the {@link HasValue#setValue(Object)} contract of
     * being the same as {@link HasValue#setValue(Object, boolean)} with false.
     */
    @Override
    public final void setValue(T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        this.value = value;

        update();

        if (fireEvents) {
            fireEvent(new SimpleValueChangeEvent<T>(value));
        }
    }

    /**
     * Update the GUI's value to match {@link #getValue()}. Do so without firing
     * any events.
     */
    protected abstract void update();
}
