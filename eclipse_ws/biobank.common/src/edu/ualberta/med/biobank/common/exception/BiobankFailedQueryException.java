package edu.ualberta.med.biobank.common.exception;

public class BiobankFailedQueryException extends BiobankException {
    private static final long serialVersionUID = 1L;

    public BiobankFailedQueryException() {
        super();
    }

    public BiobankFailedQueryException(String message) {
        super(message);
    }

    public BiobankFailedQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiobankFailedQueryException(Throwable cause) {
        super(cause);
    }

}
