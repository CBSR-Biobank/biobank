package edu.ualberta.med.biobank.server.applicationservice.exceptions;


public class CollectionNotEmptyException extends BiobankSessionException {
    private static final long serialVersionUID = 1L;

    public CollectionNotEmptyException(String message) {
        super(message);
    }
}
