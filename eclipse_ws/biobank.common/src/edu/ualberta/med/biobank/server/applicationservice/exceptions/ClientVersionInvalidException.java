package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClientVersionInvalidException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ClientVersionInvalidException() {
        super();
    }

    public ClientVersionInvalidException(String message) {
        super(message);
    }

    public ClientVersionInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientVersionInvalidException(Throwable cause) {
        super(cause);
    }

}
