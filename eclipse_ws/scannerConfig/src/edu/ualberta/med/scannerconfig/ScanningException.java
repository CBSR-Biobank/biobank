package edu.ualberta.med.scannerconfig;

public class ScanningException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ScanningException(String message) {
        super(message);
    }

    public ScanningException(String message, Throwable cause) {
        super(message, cause);
    }
}
