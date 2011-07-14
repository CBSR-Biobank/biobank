package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ServerVersionOlderException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ServerVersionOlderException() {
        super();
    }

    public ServerVersionOlderException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return Messages.getString("ServerVersionOlderException.too.new.error.msg"); //$NON-NLS-1$
    }

}
