package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import edu.ualberta.med.biobank.i18n.SS;

public class ServerVersionOlderException extends BiobankServerException {

    private static final long serialVersionUID = 1L;
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

    @SuppressWarnings("nls")
    @Override
    public String getMessage() {
        return SS
            .tr("Client authentication failed. The \"{0}\" Java Client is too new to connect to a \"{1}\" server.",
                client, server).toString();
    }

}
