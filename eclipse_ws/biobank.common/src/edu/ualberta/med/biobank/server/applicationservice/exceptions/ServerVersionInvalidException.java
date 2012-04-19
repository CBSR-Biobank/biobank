package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;

public class ServerVersionInvalidException extends BiobankServerException {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    private static final LString MESSAGE =
        bundle.tr("Server version could not be determined.").format();

    public ServerVersionInvalidException() {
        super();
    }

    public ServerVersionInvalidException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return MESSAGE.toString();
    }
}
