package edu.ualberta.med.biobank.common.exception;

public class DuplicateEntryException extends BiobankCheckException {
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
