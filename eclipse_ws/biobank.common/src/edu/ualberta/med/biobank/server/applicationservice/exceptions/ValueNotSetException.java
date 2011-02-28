package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import java.text.MessageFormat;

public class ValueNotSetException extends BiobankServerException {
    private static final long serialVersionUID = 1L;

    private static final String MSG_PATTERN = "Value {0} has not been set.";

    private String propertyName;

    public ValueNotSetException(String propertyName) {
        super(MessageFormat.format(MSG_PATTERN, propertyName));
        this.propertyName = propertyName;
    }

    public ValueNotSetException(String propertyName, Throwable cause) {
        super(MessageFormat.format(MSG_PATTERN, propertyName), cause);
        this.propertyName = propertyName;
    }

    public ValueNotSetException(Throwable cause) {
        super(cause);
    }

    public String getPropertyName() {
        return propertyName;
    }
}
