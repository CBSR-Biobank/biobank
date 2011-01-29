package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import gov.nih.nci.system.applicationservice.ApplicationException;

import java.text.MessageFormat;

public class ValueNotSetException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    private static final String MSG_PATTERN = "Value {0}  has not been set.";

    public ValueNotSetException(String propertyName) {
        super(MessageFormat.format(MSG_PATTERN, propertyName));
    }

    public ValueNotSetException(String propertyName, Throwable cause) {
        super(MessageFormat.format(MSG_PATTERN, propertyName), cause);
    }

    public ValueNotSetException(Throwable cause) {
        super(cause);
    }
}
