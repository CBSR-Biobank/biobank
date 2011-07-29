package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ServerVersionNewerException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ServerVersionNewerException() {
        super();
    }

    public ServerVersionNewerException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return Messages.getString("ServerVersionNewerException.too.old.error.msg"); //$NON-NLS-1$
    }

}
