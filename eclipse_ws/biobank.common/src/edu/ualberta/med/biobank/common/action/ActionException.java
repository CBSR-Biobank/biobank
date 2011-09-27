package edu.ualberta.med.biobank.common.action;

public class ActionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ActionException(String message) {
        super(message);
    }
}
