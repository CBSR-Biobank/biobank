package edu.ualberta.med.biobank.mvp.view.item;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public abstract class BaseItem<T> implements HasValue<T> {
    private final HandlerManager handlerManager = new HandlerManager(this);

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<T> handler) {
        return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    /**
     * Finalized to agree to the {@link HasValue#setValue(Object)} contract of
     * being the same as {@link HasValue#setValue(Object, boolean)} with false.
     */
    @Override
    public final void setValue(T value) {
        setValue(value, false);
    }
}
