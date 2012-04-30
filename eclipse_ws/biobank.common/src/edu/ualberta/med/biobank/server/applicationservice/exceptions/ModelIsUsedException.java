package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;

public class ModelIsUsedException extends ActionCheckException {
    private static final long serialVersionUID = 1L;

    public ModelIsUsedException(String message) {
        super(message);
    }
}
