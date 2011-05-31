package edu.ualberta.med.biobank.common.wrappers;

public class BiobankSessionActionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BiobankSessionActionException(String message, Exception cause) {
        super(message, cause);
    }

    public BiobankSessionActionException(String message) {
        super(message);
    }
}