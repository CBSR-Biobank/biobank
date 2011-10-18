package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.ualberta.med.biobank.mvp.validation.ValidationResult;

/**
 * Event fired whenever validation was done.
 * 
 * @author jferland
 * 
 */
public class ValidationEvent extends GwtEvent<ValidationHandler> {
    private final ValidationResult validationResult;

    /**
     * Handler type.
     */
    private static Type<ValidationHandler> TYPE;

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<ValidationHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ValidationHandler>();
        }
        return TYPE;
    }

    public ValidationEvent(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    @Override
    public Type<ValidationHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ValidationHandler handler) {
        handler.onValidate(this);
    }
}
