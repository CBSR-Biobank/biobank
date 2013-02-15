package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;

public class VersionFormatInvalidException extends BiobankServerException {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    private static final LString MESSAGE =
        bundle.tr("The software version string is formatted incorrectly.").format();

    public VersionFormatInvalidException() {
        super();
    }

    public VersionFormatInvalidException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return MESSAGE.toString();
    }

}
