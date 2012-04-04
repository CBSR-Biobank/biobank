package edu.ualberta.med.biobank.common.exception;

import java.text.MessageFormat;

public class CheckFieldLimitsException extends BiobankException {

    private static final long serialVersionUID = 1L;

    private static String MESSAGE = "Field ''{0}'' should not exceeds {1} characters. Current value is ''{2}''"; 

    public CheckFieldLimitsException(String fieldName, int maxLength,
        String currentValue) {
        super(MessageFormat.format(MESSAGE, fieldName, maxLength, currentValue));
    }

    public CheckFieldLimitsException(String fieldName, int maxLength,
        String currentValue, Throwable cause) {
        super(
            MessageFormat.format(MESSAGE, fieldName, maxLength, currentValue),
            cause);
    }

}
