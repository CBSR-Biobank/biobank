package edu.ualberta.med.biobank.event;

/**
 * Returned from methods that add a handler for an event. Used to remove the handler.
 * 
 * @author jferland
 *
 */
public interface HandlerRegistration {
	public void removeHandler();
}
