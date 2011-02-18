package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ServerVersionOlderException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ServerVersionOlderException(String message) {
        super(message);
    }

    public ServerVersionOlderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerVersionOlderException(Throwable cause) {
        super(cause);
    }

}
