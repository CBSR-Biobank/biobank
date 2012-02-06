package edu.ualberta.med.biobank.validator;

import javax.validation.ConstraintValidatorContext;

import org.hibernate.Session;

public abstract class SessionAwareConstraintValidator<T> {
    private Session session;

    public SessionAwareConstraintValidator() {
    }

    public void setSession(Session session) {
        this.session = session;
    }

    protected Session getSession() {
        return session;
    }

    public boolean isValid(T value, ConstraintValidatorContext context) {
        boolean result = isValidInSession(value, context);
        return result;
    }

    public abstract boolean isValidInSession(T value,
        ConstraintValidatorContext context);
}
