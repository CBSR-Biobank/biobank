package edu.ualberta.med.biobank.common.action.exception;


/**
 * 
 * @author Jonathan Ferland
 */
public class ActionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
