package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import gov.nih.nci.system.applicationservice.ApplicationException;

public class BiobankServerException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public BiobankServerException() {
        super();
    }

    public BiobankServerException(String message) {
        super(message);
    }

    public BiobankServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiobankServerException(Throwable cause) {
        super(cause);
    }

}
