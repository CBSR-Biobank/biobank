package edu.ualberta.med.biobank.common.exception;

public class ContainerLabelSearchException extends BiobankCheckException {

    private static final long serialVersionUID = 1L;

    public ContainerLabelSearchException(String message) {
        super(message);
    }

    public ContainerLabelSearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerLabelSearchException(Throwable cause) {
        super(cause);
    }
}
