package edu.ualberta.med.biobank.common.action.exception;

// TODO: handle i18n on Exception messages?
public class ActionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Exception cause) {
        super(message, cause);
    }
}
