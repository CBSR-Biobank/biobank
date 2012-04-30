package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class NullPropertyException extends BiobankSessionException {
    private static final long serialVersionUID = 1L;

    public NullPropertyException(String message) {
        super(message);
    }
}
