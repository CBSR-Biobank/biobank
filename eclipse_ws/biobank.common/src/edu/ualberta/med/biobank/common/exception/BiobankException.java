package edu.ualberta.med.biobank.common.exception;

public class BiobankException extends Exception {
    private static final long serialVersionUID = 1L;

    public BiobankException() {
        super();
    }

    public BiobankException(String message) {
        super(message);
    }

    public BiobankException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiobankException(Throwable cause) {
        super(cause);
    }
}
