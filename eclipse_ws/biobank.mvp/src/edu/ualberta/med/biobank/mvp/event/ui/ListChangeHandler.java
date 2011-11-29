package edu.ualberta.med.biobank.mvp.event.ui;

import com.google.gwt.event.shared.EventHandler;

public interface ListChangeHandler<E> extends EventHandler {
    void onListChange(ListChangeEvent<E> event);
}
