package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class SimpleValueChangeEvent<T> extends ValueChangeEvent<T> {
    public SimpleValueChangeEvent(T value) {
        super(value);
    }
}
