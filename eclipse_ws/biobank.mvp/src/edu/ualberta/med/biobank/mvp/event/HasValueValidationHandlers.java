package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * A widget that implements this interface is a public source of
 * {@link ValueValidationEvent} events.
 * 
 * @param <T> the value about to be changed
 */
public interface HasValueValidationHandlers<T> extends HasHandlers {
  /**
   * Adds a {@link ValueValidationEvent} handler.
   * 
   * @param handler the handler
   * @return the registration for the event
   */
  HandlerRegistration addValueValidationHandler(ValueValidationEvent<T> handler);
}