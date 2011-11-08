package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Exception event, probably to alert (notify) the user of something.
 * 
 * @author jferland
 * 
 */
public class ExceptionEvent extends GwtEvent<ExceptionHandler> {
    private final Throwable throwable;

    /**
     * Handler type.
     */
    private static Type<ExceptionHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<ExceptionHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ExceptionHandler>();
        }
        return TYPE;
    }

    public ExceptionEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public Type<ExceptionHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ExceptionHandler handler) {
        handler.onException(this);
    }
}
