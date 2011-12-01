package edu.ualberta.med.biobank.mvp.user.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

import edu.ualberta.med.biobank.mvp.event.ui.ListChangeHandler;

public interface HasListChangeHandlers<E> extends HasHandlers {
    HandlerRegistration addListChangeHandler(ListChangeHandler<E> handler);
}
