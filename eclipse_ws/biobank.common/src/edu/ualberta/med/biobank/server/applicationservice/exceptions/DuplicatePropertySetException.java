package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;

public class DuplicatePropertySetException extends ActionCheckException {
    private static final long serialVersionUID = 1L;

    public DuplicatePropertySetException(String message) {
        super(message);
    }
}
