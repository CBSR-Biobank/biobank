package edu.ualberta.med.biobank.server.applicationservice.exceptions;


public class BiobankDataErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BiobankDataErrorException(String message) {
        super(message);
    }

    public BiobankDataErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiobankDataErrorException(Throwable cause) {
        super(cause);
    }
}
