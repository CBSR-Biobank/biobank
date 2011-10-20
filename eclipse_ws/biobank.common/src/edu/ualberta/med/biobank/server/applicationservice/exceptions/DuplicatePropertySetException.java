package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.common.action.exception.ActionException;

public class DuplicatePropertySetException extends ActionException {
    private static final long serialVersionUID = 1L;

    public DuplicatePropertySetException(String message) {
        super(message);
    }
}
