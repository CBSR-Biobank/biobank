package edu.ualberta.med.biobank.server.interceptor;

import java.sql.BatchUpdateException;

import org.hibernate.PropertyValueException;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import edu.ualberta.med.biobank.common.exception.ExceptionUtils;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankServerException;
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
     * 
     * @throws ValidationException
     */
    public void afterThrowing(InvalidStateException ise)
        throws ValidationException {
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

    public void afterThrowing(ApplicationException ae)
        throws ApplicationException {
        Throwable cause = ExceptionUtils.findCausesInThrowable(ae,
            PropertyValueException.class, ValueNotSetException.class);
        getNotNullPropertyValueException(cause, ae);
        if (cause != null && cause instanceof ValueNotSetException) {
            ValueNotSetException pve = (ValueNotSetException) cause;
            throw new ValueNotSetException(pve.getPropertyName(), ae);
        }
        throw ae;
    }

    public void afterThrowing(DataIntegrityViolationException dive)
        throws BiobankServerException {
        Throwable cause = ExceptionUtils.findCausesInThrowable(dive,
            PropertyValueException.class, BatchUpdateException.class);
        getNotNullPropertyValueException(cause, dive);
        if (cause != null && cause instanceof BatchUpdateException) {
            BatchUpdateException bue = (BatchUpdateException) cause;
            throw new BiobankServerException(bue.getMessage(), dive);
        }
        throw dive;
    }

    private void getNotNullPropertyValueException(Throwable cause,
        Throwable originalMainEx) throws ValueNotSetException {
        if (cause != null && cause instanceof PropertyValueException) {
            PropertyValueException pve = (PropertyValueException) cause;
            if (pve.getMessage().startsWith("not-null"))
                throw new ValueNotSetException(pve.getPropertyName(),
                    originalMainEx);
        }
    }

}
