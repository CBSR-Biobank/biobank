package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.i18n.LocalizedString;

public class ClientVersionInvalidException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public ClientVersionInvalidException() {
        super();
    }

    public ClientVersionInvalidException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("nls")
    @Override
    public String getMessage() {
        return LocalizedString
            .tr("Client authentication failed. The Java Client version is not compatible with the server and must be upgraded.")
            .toString();
    }
}
