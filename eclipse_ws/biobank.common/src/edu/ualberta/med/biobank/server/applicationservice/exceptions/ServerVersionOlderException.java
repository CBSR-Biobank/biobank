package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Tr;

public class ServerVersionOlderException extends BiobankServerException {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    private static final Tr MESSAGE =
        bundle.tr("Client authentication failed. The \"{0}\" Java Client" +
            " is too new to connect to a \"{1}\" server.");

    private String server;
    private String client;

    public ServerVersionOlderException(String server, String client) {
        super();
        this.server = server;
        this.client = client;
    }

    public ServerVersionOlderException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return MESSAGE.format(client, server).toString();
    }

}
