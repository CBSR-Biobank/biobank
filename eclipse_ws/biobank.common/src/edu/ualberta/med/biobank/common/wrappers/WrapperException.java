package edu.ualberta.med.biobank.common.wrappers;

public class WrapperException extends Exception {
    private static final long serialVersionUID = 1L;

    public WrapperException() {
        super();
    }

    public WrapperException(String message) {
        super(message);
    }

    public WrapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrapperException(Throwable cause) {
        super(cause);
    }
}
