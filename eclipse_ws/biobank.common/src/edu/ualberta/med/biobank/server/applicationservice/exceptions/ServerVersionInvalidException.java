package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ServerVersionInvalidException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ServerVersionInvalidException() {
        super();
    }

    public ServerVersionInvalidException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return Messages
            .getString("ServerVersionInvalidException.server.version.error.msg"); //$NON-NLS-1$
    }
}
