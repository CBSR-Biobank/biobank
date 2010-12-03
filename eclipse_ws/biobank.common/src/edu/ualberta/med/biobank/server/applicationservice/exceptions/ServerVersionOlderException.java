package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import gov.nih.nci.system.applicationservice.ApplicationException;

public class ServerVersionOlderException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ServerVersionOlderException() {
        super();
    }

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
