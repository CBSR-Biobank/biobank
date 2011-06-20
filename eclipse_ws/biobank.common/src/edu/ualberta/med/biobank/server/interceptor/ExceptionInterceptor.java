package edu.ualberta.med.biobank.server.interceptor;

import java.sql.BatchUpdateException;

import org.hibernate.PropertyValueException;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import edu.ualberta.med.biobank.common.exception.ExceptionUtils;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankServerException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.StringValueLengthServerException;
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
        StringBuffer message = new StringBuffer();
        for (int i = 0; i < ise.getInvalidValues().length; i++) {
            InvalidValue iv = ise.getInvalidValues()[i];
            message.append(iv.getBeanClass().getSimpleName()).append(": ") //$NON-NLS-1$
                .append(iv.getPropertyName()).append(" ") //$NON-NLS-1$
                .append(iv.getMessage());
            if (i != ise.getInvalidValues().length - 1)
                message.append(". "); //$NON-NLS-1$
        }
        throw new ValidationException(message.toString(), ise);
    }

    public void afterThrowing(ApplicationException ae)
        throws ApplicationException {
        Throwable cause = ExceptionUtils.findCausesInThrowable(ae,
            PropertyValueException.class, ValueNotSetException.class);
        getNotNullPropertyValueException(cause, ae);
        if (cause != null && cause instanceof ValueNotSetException) {
            ValueNotSetException vnse = (ValueNotSetException) cause;
            throw new ValueNotSetException(vnse.getPropertyName(),
                vnse.getObjectName(), ae);
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
            if (pve.getMessage().startsWith("not-null")) { //$NON-NLS-1$
                String objectName = pve.getEntityName();
                int i = objectName.lastIndexOf("."); //$NON-NLS-1$
                throw new ValueNotSetException(pve.getPropertyName(),
                    objectName.substring(i + 1), originalMainEx);
            }
        }
    }

    public void afterThrowing(InvalidDataAccessResourceUsageException idarue)
        throws BiobankServerException {
        Throwable cause = ExceptionUtils.findCausesInThrowable(idarue,
            BatchUpdateException.class);
        if (cause != null && cause instanceof BatchUpdateException) {
            BatchUpdateException bue = (BatchUpdateException) cause;
            if (bue.getMessage().contains("Data too long for column")) { //$NON-NLS-1$
                throw new StringValueLengthServerException(bue.getMessage());
            }
        }
    }
}
