package edu.ualberta.med.biobank.validator;

import javax.validation.ConstraintValidatorContext;

import org.hibernate.event.EventSource;

public abstract class EventSourceAwareConstraintValidator<T> {
    private EventSource eventSource;

    public EventSourceAwareConstraintValidator() {
    }

    public void setSession(EventSource eventSource) {
        this.eventSource = eventSource;
    }

    protected EventSource getEventSource() {
        return eventSource;
    }

    public boolean isValid(T value, ConstraintValidatorContext context) {
        boolean result = isValidInEventSource(value, context);
        return result;
    }

    public abstract boolean isValidInEventSource(T value,
        ConstraintValidatorContext context);
}
