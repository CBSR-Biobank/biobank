package edu.ualberta.med.biobank.mvp.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a value validation event.
 * 
 * @param <T>
 *            the value about to be validated
 */
public class ValueValidationEvent<T> extends
    GwtEvent<ValueValidationHandler<T>> {

    /**
     * Handler type.
     */
    private static Type<ValueValidationHandler<?>> TYPE;

    /**
     * Fires a value validation event on all registered handlers in the handler
     * manager. If no such handlers exist, this method will do nothing.
     * 
     * @param <T>
     *            the old value type
     * @param source
     *            the source of the handlers
     * @param value
     *            the value
     */
    public static <T> void fire(HasValueValidationHandlers<T> source, T value) {
        if (TYPE != null) {
            ValueValidationEvent<T> event = new ValueValidationEvent<T>(value);
            source.fireEvent(event);
        }
    }

    /**
     * Fires value validation event if the old value is not equal to the new
     * value. Use this call rather than making the decision to short circuit
     * yourself for safe handling of null.
     * 
     * @param <T>
     *            the old value type
     * @param source
     *            the source of the handlers
     * @param oldValue
     *            the oldValue, may be null
     * @param newValue
     *            the newValue, may be null
     */
    public static <T> void fireIfNotEqual(HasValueValidationHandlers<T> source,
        T oldValue, T newValue) {
        if (shouldFire(source, oldValue, newValue)) {
            ValueValidationEvent<T> event =
                new ValueValidationEvent<T>(newValue);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<ValueValidationHandler<?>> getType() {
        if (TYPE == null) {
            TYPE = new Type<ValueValidationHandler<?>>();
        }
        return TYPE;
    }

    /**
     * Convenience method to allow subtypes to know when they should fire a
     * value validation event in a null-safe manner.
     * 
     * @param <T>
     *            value type
     * @param source
     *            the source
     * @param oldValue
     *            the old value
     * @param newValue
     *            the new value
     * @return whether the event should be fired
     */
    protected static <T> boolean shouldFire(
        HasValueValidationHandlers<T> source, T oldValue, T newValue) {
        return TYPE != null && oldValue != newValue
            && (oldValue == null || !oldValue.equals(newValue));
    }

    private final T value;

    /**
     * Creates a value validation event.
     * 
     * @param value
     *            the value
     */
    protected ValueValidationEvent(T value) {
        this.value = value;
    }

    // The instance knows its BeforeSelectionHandler is of type I, but the TYPE
    // field itself does not, so we have to do an unsafe cast here.
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public final Type<ValueValidationHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    public T getValue() {
        return value;
    }

    @Override
    public String toDebugString() {
        return super.toDebugString() + getValue();
    }

    @Override
    protected void dispatch(ValueValidationHandler<T> handler) {
        handler.onValueValidation(this);
    }
}
