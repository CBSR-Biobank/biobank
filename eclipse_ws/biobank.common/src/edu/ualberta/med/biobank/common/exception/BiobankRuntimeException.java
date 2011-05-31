package edu.ualberta.med.biobank.common.exception;

public class BiobankRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BiobankRuntimeException() {
        super();
    }

    public BiobankRuntimeException(String message) {
        super(message);
    }

    public BiobankRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiobankRuntimeException(Throwable cause) {
        super(cause);
    }

}
