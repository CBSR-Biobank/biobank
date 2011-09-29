package edu.ualberta.med.biobank.common.exception;

import java.text.MessageFormat;

public class NoRightForKeyDescException extends BiobankException {
    private static final long serialVersionUID = 1L;

    private final static String MSG = "No right exists with keyDesc=''{0}''"; //$NON-NLS-1$

    public NoRightForKeyDescException(String keyDesc) {
        super(MessageFormat.format(MSG, keyDesc));
    }

    public NoRightForKeyDescException(String keyDesc, Throwable cause) {
        super(MessageFormat.format(MSG, keyDesc), cause);
    }

}