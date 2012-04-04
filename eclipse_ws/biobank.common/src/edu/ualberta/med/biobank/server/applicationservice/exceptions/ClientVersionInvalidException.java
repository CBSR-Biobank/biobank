package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ClientVersionInvalidException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ClientVersionInvalidException() {
        super();
    }

    public ClientVersionInvalidException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Client authentication failed. The Java Client version is not compatible with the server and must be upgraded."; 
    }
}
