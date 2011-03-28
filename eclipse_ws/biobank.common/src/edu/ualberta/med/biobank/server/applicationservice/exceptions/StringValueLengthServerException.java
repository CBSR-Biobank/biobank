package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class StringValueLengthServerException extends BiobankServerException {
    private static final long serialVersionUID = 1L;

    public StringValueLengthServerException(String message) {
        super(message);
    }

    public StringValueLengthServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public StringValueLengthServerException(Throwable cause) {
        super(cause);
    }
}
