package edu.ualberta.med.biobank.common.action.exception;

public class ActionCheckException extends ActionException {

    private static final long serialVersionUID = 1L;

    public ActionCheckException(String message) {
        super(message);
    }

    public ActionCheckException(Throwable cause) {
        super(cause);
    }

    public ActionCheckException(String message, Throwable cause) {
        super(message, cause);
    }

}
