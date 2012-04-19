package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;

public class ClientVersionInvalidException extends BiobankServerException {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    private static final LString MESSAGE =
        bundle.tr("Client authentication failed. The Java Client version is" +
            " not compatible with the server and must be upgraded.").format();

    public ClientVersionInvalidException() {
        super();
    }

    public ClientVersionInvalidException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return MESSAGE.toString();
    }
}
