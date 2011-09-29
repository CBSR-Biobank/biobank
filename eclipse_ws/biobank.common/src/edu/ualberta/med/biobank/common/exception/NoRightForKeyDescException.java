package edu.ualberta.med.biobank.common.exception;

import java.text.MessageFormat;

public class NoRightForKeyDescException extends BiobankException {
    private static final long serialVersionUID = 1L;

    private final static String MSG = Messages
        .getString("NoRightForKeyDescException.msg"); //$NON-NLS-1$

    public NoRightForKeyDescException(String keyDesc) {
        super(MessageFormat.format(MSG, keyDesc));
    }

    public NoRightForKeyDescException(String keyDesc, Throwable cause) {
        super(MessageFormat.format(MSG, keyDesc), cause);
    }

}