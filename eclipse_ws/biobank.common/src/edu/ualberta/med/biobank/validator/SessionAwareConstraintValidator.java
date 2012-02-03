package edu.ualberta.med.biobank.validator;

import javax.validation.ConstraintValidatorContext;

import org.hibernate.FlushMode;
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
        // TODO: may need to open a new Session or transaction? HRM?
        // See:
        // https://community.jboss.org/wiki/AccessingtheHibernateSessionwithinaConstraintValidator
        // Problem: if the implementation of isValidInSession causes a flush,
        // then we will be called again. But we cannot just set FlushMode.NEVER
        // because certain changes must be flushed so that an SQL query does not
        // return incorrect results from a select (e.g. renaming a unique
        // field).
        FlushMode oldMode = session.getFlushMode();
        try {
            session.setFlushMode(FlushMode.MANUAL);

            boolean result = isValidInSession(value, context);
            return result;
        } finally {
            session.setFlushMode(oldMode);
        }
    }

    public abstract boolean isValidInSession(T value,
        ConstraintValidatorContext context);
}
