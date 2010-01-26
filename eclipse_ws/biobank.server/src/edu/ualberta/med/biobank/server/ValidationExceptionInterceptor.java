package edu.ualberta.med.biobank.server;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.springframework.aop.ThrowsAdvice;

public class ValidationExceptionInterceptor implements ThrowsAdvice {

    public void afterThrowing(InvalidStateException ise) {
        String message = "";
        for (InvalidValue iv : ise.getInvalidValues()) {
            message += iv.getMessage() + " - ";
        }
        throw new RuntimeException(message, ise);
    }
}
