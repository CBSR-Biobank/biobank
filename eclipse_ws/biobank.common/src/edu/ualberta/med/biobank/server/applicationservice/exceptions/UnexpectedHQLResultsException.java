package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class UnexpectedHQLResultsException extends BiobankSessionException {
    private static final long serialVersionUID = 1L;

    public UnexpectedHQLResultsException(String message) {
        super(message);
    }

}
