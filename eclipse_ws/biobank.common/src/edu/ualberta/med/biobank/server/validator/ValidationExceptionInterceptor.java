package edu.ualberta.med.biobank.server.validator;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.springframework.aop.ThrowsAdvice;

public class ValidationExceptionInterceptor implements ThrowsAdvice {

    public void afterThrowing(InvalidStateException ise) {
        String message = "";
        for (int i = 0; i < ise.getInvalidValues().length; i++) {
            InvalidValue iv = ise.getInvalidValues()[i];
            message += iv.getBeanClass().getSimpleName() + ": "
                + iv.getPropertyName() + " " + iv.getMessage();
            if (i != ise.getInvalidValues().length - 1)
                message += ". ";
        }
        throw new RuntimeException(message, ise);
    }
}
