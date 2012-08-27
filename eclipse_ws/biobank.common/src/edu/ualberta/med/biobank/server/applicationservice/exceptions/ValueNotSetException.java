package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import java.text.MessageFormat;

@SuppressWarnings("nls")
@Deprecated
public class ValueNotSetException extends BiobankServerException {
    private static final long serialVersionUID = 1L;

    private static final String MSG_PATTERN =
        "Value ''{0}'' has not been set on ''{1}''.";

    private String propertyName;

    private String objectName;

    public ValueNotSetException(String propertyName, String objectName) {
        super(MessageFormat.format(MSG_PATTERN, propertyName, objectName));
        this.propertyName = propertyName;
        this.objectName = objectName;
    }

    public ValueNotSetException(String propertyName, String objectName,
        Throwable cause) {
        super(MessageFormat.format(MSG_PATTERN, propertyName, objectName),
            cause);
        this.propertyName = propertyName;
        this.objectName = objectName;
    }

    public ValueNotSetException(Throwable cause) {
        super(cause);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getObjectName() {
        return objectName;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format(MSG_PATTERN, propertyName, objectName);
    }

}
