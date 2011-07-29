package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;

/**
 * Contains the message of a hibernate validation. There is no translation: the
 * InvalidValue object already contain the English message.
 */
public class ValidationException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    private String message;

    public ValidationException(InvalidStateException ise) {
        super(ise);
        StringBuffer message = new StringBuffer();
        for (int i = 0; i < ise.getInvalidValues().length; i++) {
            InvalidValue iv = ise.getInvalidValues()[i];
            message.append(iv.getBeanClass().getSimpleName()).append(": ") //$NON-NLS-1$
                .append(iv.getPropertyName()).append(" ") //$NON-NLS-1$
                .append(iv.getMessage());
            if (i != ise.getInvalidValues().length - 1)
                message.append(". "); //$NON-NLS-1$
        }
        this.message = message.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }

}
