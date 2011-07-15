package edu.ualberta.med.biobank.common.exception;

public class BiobankQueryResultSizeException extends BiobankException {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MSG = "Invalid size for HQL query result"; //$NON-NLS-1$

    public BiobankQueryResultSizeException() {
        super(DEFAULT_MSG);
    }

    public BiobankQueryResultSizeException(String message) {
        super(message);
    }

    public BiobankQueryResultSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BiobankQueryResultSizeException(Throwable cause) {
        super(DEFAULT_MSG, cause);
    }
}
