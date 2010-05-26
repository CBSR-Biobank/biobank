package edu.ualberta.med.biobank.common;

public class BiobankStringLengthException extends Exception {

    private static final long serialVersionUID = 1L;

    public BiobankStringLengthException(String message) {
        super(message);
    }

    public BiobankStringLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiobankStringLengthException(Throwable cause) {
        super(cause);
    }

}
