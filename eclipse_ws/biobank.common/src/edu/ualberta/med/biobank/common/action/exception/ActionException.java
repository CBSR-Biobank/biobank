package edu.ualberta.med.biobank.common.action.exception;

public class ActionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionException(Throwable cause) {
        super(cause);
    }
}
