package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class VersionInvalidException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public VersionInvalidException(String message) {
        super(message);
    }

    public VersionInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public VersionInvalidException(Throwable cause) {
        super(cause);
    }

}
