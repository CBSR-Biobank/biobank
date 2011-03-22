package edu.ualberta.med.biobank.common.exception;

public class BiobankDeleteException extends BiobankCheckException {

    private static final long serialVersionUID = 1L;

    public BiobankDeleteException() {
        super();
    }

    public BiobankDeleteException(String message) {
        super(message);
    }

    public BiobankDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiobankDeleteException(Throwable cause) {
        super(cause);
    }

}
