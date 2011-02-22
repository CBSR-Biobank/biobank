package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ServerVersionNewerException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ServerVersionNewerException(String message) {
        super(message);
    }

    public ServerVersionNewerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerVersionNewerException(Throwable cause) {
        super(cause);
    }

}
