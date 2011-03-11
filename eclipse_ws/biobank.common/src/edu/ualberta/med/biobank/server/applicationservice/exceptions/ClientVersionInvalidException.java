package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ClientVersionInvalidException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ClientVersionInvalidException(String message) {
        super(message);
    }

    public ClientVersionInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientVersionInvalidException(Throwable cause) {
        super(cause);
    }

}
