package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ModificationConcurrencyException extends BiobankServerException {
    private static final long serialVersionUID = 1L;

    public ModificationConcurrencyException() {
        super();
    }

    public ModificationConcurrencyException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "An object has already been modified by another user"; 
    }
}
