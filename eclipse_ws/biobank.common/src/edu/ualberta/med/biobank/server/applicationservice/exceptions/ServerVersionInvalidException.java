package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.i18n.LString;

public class ServerVersionInvalidException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ServerVersionInvalidException() {
        super();
    }

    public ServerVersionInvalidException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("nls")
    @Override
    public String getMessage() {
        return LString.tr("Server version could not be determined.").toString();
    }
}
