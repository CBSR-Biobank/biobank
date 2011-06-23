package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import gov.nih.nci.system.dao.DAOException;

public class BiobankSessionException extends DAOException {
    private static final long serialVersionUID = 1L;

    public BiobankSessionException(String message) {
        super(message);
    }

    public BiobankSessionException(String message, Exception cause) {
        super(message, cause);
    }
}
