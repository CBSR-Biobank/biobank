package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Tr;

public class ServerVersionNewerException extends BiobankServerException {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    private static final Tr MESSAGE =
        bundle.tr("Client authentication failed. The \"{0}\" Java Client" +
            " is too old to connect to a \"{1}\" server.");

    private String server;
    private String client;

    public ServerVersionNewerException(String server, String client) {
        super();
        this.server = server;
        this.client = client;
    }

    public ServerVersionNewerException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return MESSAGE.format(client, server).getString();
    }
}
