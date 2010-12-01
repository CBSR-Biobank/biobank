package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import gov.nih.nci.system.applicationservice.ApplicationException;

public class ServerVersionInvalidException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ServerVersionInvalidException() {
        super();
    }

    public ServerVersionInvalidException(String message) {
        super(message);
    }

    public ServerVersionInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerVersionInvalidException(Throwable cause) {
        super(cause);
    }

}
