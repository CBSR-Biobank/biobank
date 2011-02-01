package edu.ualberta.med.biobank.server.applicationservice.exceptions;


public class DuplicateEntryException extends BiobankDataErrorException {
    private static final long serialVersionUID = 1L;

    public DuplicateEntryException(String message) {
        super(message);
    }

    public DuplicateEntryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateEntryException(Throwable cause) {
        super(cause);
    }

}
