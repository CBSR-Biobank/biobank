package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.i18n.LTemplate;

public class ServerVersionOlderException extends BiobankServerException {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final LTemplate.Tr MESSAGE =
        LTemplate.tr("Client authentication failed. The \"{0}\" Java Client" +
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
