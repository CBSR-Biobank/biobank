package edu.ualberta.med.biobank.common.action.exception;

public class AccessDeniedException extends ActionException {

    private static final long serialVersionUID = 1L;

    public AccessDeniedException() {
        super("Access denied"); //$NON-NLS-1$
    }
}
