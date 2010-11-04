package edu.ualberta.med.biobank.server.applicationservice;

import gov.nih.nci.system.applicationservice.ApplicationException;

public class VersionIncompatibilityException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public VersionIncompatibilityException() {
        super();
    }

    public VersionIncompatibilityException(String message) {
        super(message);
    }

    public VersionIncompatibilityException(String message, Throwable cause) {
        super(message, cause);
    }

    public VersionIncompatibilityException(Throwable cause) {
        super(cause);
    }

}
