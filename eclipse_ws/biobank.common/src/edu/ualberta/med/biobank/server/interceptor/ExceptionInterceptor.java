package edu.ualberta.med.biobank.server.interceptor;

import org.hibernate.PropertyValueException;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import edu.ualberta.med.biobank.common.exception.ExceptionUtils;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValidationException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Intercept exceptions thrown by application service methods
 */
public class ExceptionInterceptor implements ThrowsAdvice {

    /**
     * Intercept Hibernate validation errors to throw an exception with a better
     * message.
     */
    public void afterThrowing(InvalidStateException ise) {
        String message = "";
        for (int i = 0; i < ise.getInvalidValues().length; i++) {
            InvalidValue iv = ise.getInvalidValues()[i];
            message += iv.getBeanClass().getSimpleName() + ": "
                + iv.getPropertyName() + " " + iv.getMessage();
            if (i != ise.getInvalidValues().length - 1)
                message += ". ";
        }
        throw new ValidationException(message, ise);
    }

    public void afterThrowing(ApplicationException ae) throws Throwable {
        findNotNullPropertyValueException(ae);
    }

    public void afterThrowing(DataIntegrityViolationException dive)
        throws Throwable {
        findNotNullPropertyValueException(dive);
    }

    private void findNotNullPropertyValueException(Throwable t)
        throws Throwable {
        Throwable cause = ExceptionUtils.findCauseInThrowable(t,
            PropertyValueException.class);
        if (cause != null) {
            PropertyValueException pve = (PropertyValueException) cause;
            if (pve.getMessage().startsWith("not-null"))
                throw new ValueNotSetException(pve.getPropertyName(), t);
        }
        throw t;
    }
}
