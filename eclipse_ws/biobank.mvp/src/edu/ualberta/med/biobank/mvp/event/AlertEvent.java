package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Alert event, probably to alert (notify) the user of something.
 * 
 * @author jferland
 * 
 */
public class AlertEvent extends GwtEvent<AlertHandler> {
    private final String message;

    /**
     * Handler type.
     */
    private static Type<AlertHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<AlertHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<AlertHandler>();
        }
        return TYPE;
    }

    public AlertEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Type<AlertHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AlertHandler handler) {
        handler.onAlert(this);
    }

}
