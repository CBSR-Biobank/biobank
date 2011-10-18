package edu.ualberta.med.biobank.common.action.exception;

public class NotFoundException extends ActionException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }

}
