package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class VersionFormatInvalidException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public VersionFormatInvalidException() {
        super();
    }

    public VersionFormatInvalidException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "The version string is formatted incorrectly."; 
    }

}
