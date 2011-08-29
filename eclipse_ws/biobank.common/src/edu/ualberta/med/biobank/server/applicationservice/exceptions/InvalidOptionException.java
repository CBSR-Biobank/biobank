package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class InvalidOptionException extends BiobankSessionException {
    private static final long serialVersionUID = 1L;

    public InvalidOptionException(String message) {
        super(message);
    }
}