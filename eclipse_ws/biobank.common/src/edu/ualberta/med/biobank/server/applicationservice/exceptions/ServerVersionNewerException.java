package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import gov.nih.nci.system.applicationservice.ApplicationException;

public class ServerVersionNewerException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ServerVersionNewerException() {
        super();
    }

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
