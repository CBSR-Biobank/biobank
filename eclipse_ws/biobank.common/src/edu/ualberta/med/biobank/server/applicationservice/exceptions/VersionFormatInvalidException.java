package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.server.applicationservice.Messages;

public class VersionFormatInvalidException extends BiobankServerException {

    private static final long serialVersionUID = 1L;

    public VersionFormatInvalidException() {
        super();
    }

    public VersionFormatInvalidException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return Messages
            .getString("VersionFormatInvalidException.version.format.error.msg"); //$NON-NLS-1$
    }

}
