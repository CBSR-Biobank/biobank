package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import java.text.MessageFormat;

public class ServerVersionNewerException extends BiobankServerException {

    private static final long serialVersionUID = 1L;
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
        return MessageFormat
            .format(
                Messages
                    .getString("ServerVersionNewerException.too.old.error.msg"), client, server); //$NON-NLS-1$
    }

}
