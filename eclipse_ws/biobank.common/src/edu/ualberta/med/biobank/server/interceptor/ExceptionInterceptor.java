package edu.ualberta.med.biobank.server.interceptor;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.exception.ExceptionUtils;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import gov.nih.nci.system.applicationservice.ApplicationException;

import org.hibernate.PropertyValueException;
import org.springframework.aop.ThrowsAdvice;

/**
 * Intercept exceptions thrown by application service methods
 */
public class ExceptionInterceptor implements ThrowsAdvice {

    // /**
    // * Intercept Hibernate validation errors to throw an exception with a
    // better
    // * message.
    // *
    // * @throws ValidationException
    // */
    // public void afterThrowing(InvalidStateException ise)
    // throws ValidationException {
    // throw new ValidationException(ise);
    // }
    //
    public void afterThrowing(ApplicationException ae)
        throws ApplicationException {
        Throwable cause = ExceptionUtils.findCausesInThrowable(ae,
            PropertyValueException.class, ActionException.class);
        // ,
        // ValueNotSetException.class, // ConstraintViolationException.class);
        throwNotNullPropertyValueException(cause, ae);
        // if (cause != null && cause instanceof ValueNotSetException) {
        // ValueNotSetException vnse = (ValueNotSetException) cause;
        // throw new ValueNotSetException(vnse.getPropertyName(),
        // vnse.getObjectName(), ae);
        // }
        if (cause != null && cause instanceof ActionException) {
            throw (ActionException) cause;
        }
        // if (cause != null && cause instanceof ConstraintViolationException) {
        // Throwable orig = ExceptionUtils.findFirstCause(cause);
        // throw new BiobankServerException(orig.getMessage(), ae);
        // }
        //
        throw ae;
    }

    //
    // public void afterThrowing(DataIntegrityViolationException dive)
    // throws BiobankServerException {
    // Throwable cause = ExceptionUtils.findCausesInThrowable(dive,
    // PropertyValueException.class, BatchUpdateException.class);
    // getNotNullPropertyValueException(cause, dive);
    // if (cause != null && cause instanceof BatchUpdateException) {
    // BatchUpdateException bue = (BatchUpdateException) cause;
    // throw new BiobankServerException(bue.getMessage(), dive);
    // }
    // throw dive;
    // }

    private void throwNotNullPropertyValueException(Throwable cause,
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
    //
    // public void afterThrowing(InvalidDataAccessResourceUsageException idarue)
    // throws BiobankServerException {
    // Throwable cause = ExceptionUtils.findCausesInThrowable(idarue,
    // BatchUpdateException.class);
    // if (cause != null && cause instanceof BatchUpdateException) {
    // BatchUpdateException bue = (BatchUpdateException) cause;
    //            if (bue.getMessage().contains("Data too long for column")) { //$NON-NLS-1$
    // throw new StringValueLengthServerException(bue.getMessage());
    // }
    // }
    // throw idarue;
    // }
    //
    // public void afterThrowing(HibernateOptimisticLockingFailureException
    // holfe)
    // throws BiobankServerException {
    // Throwable cause = ExceptionUtils.findCausesInThrowable(holfe,
    // StaleStateException.class);
    // if (cause != null && cause instanceof StaleStateException) {
    // throw new ModificationConcurrencyException(holfe);
    // }
    // throw holfe;
    // }
}
