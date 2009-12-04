package edu.ualberta.med.biobank.exception;

public class UserUIException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserUIException() {
        super();
    }

    public UserUIException(String message) {
        super(message);
    }

    public UserUIException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserUIException(Throwable cause) {
        super(cause);
    }
}
