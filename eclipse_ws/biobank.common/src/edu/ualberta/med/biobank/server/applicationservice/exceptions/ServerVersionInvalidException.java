package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ServerVersionInvalidException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ServerVersionInvalidException(String message) {
        super(message);
    }

    public ServerVersionInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerVersionInvalidException(Throwable cause) {
        super(cause);
    }

}
