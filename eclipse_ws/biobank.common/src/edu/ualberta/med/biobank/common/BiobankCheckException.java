package edu.ualberta.med.biobank.common;

public class BiobankCheckException extends Exception {

    private static final long serialVersionUID = 1L;

    public BiobankCheckException() {
        super();
    }

    public BiobankCheckException(String message) {
        super(message);
    }

    public BiobankCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiobankCheckException(Throwable cause) {
        super(cause);
    }
}
