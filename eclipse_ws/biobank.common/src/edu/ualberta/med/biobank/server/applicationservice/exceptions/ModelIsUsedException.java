package edu.ualberta.med.biobank.server.applicationservice.exceptions;

public class ModelIsUsedException extends BiobankSessionException {
    private static final long serialVersionUID = 1L;

    public ModelIsUsedException(String message) {
        super(message);
    }
}
