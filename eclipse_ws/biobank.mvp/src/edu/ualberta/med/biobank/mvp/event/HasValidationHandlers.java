package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * A widget that implements this interface is a public source of
 * {@link ValidationEvent} events.
 * 
 */
public interface HasValidationHandlers extends HasHandlers {
    /**
     * Adds a {@link ValidationEvent} handler.
     * 
     * @param handler
     *            the handler
     * @return the registration for the event
     */
    HandlerRegistration addValidationHandler(ValidationHandler handler);
}