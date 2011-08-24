package edu.ualberta.med.biobank.server.applicationservice.exceptions;

import java.text.MessageFormat;

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

    @Override
    public String getMessage() {
        return MessageFormat
            .format(
                Messages
                    .getString("ServerVersionOlderException.too.new.error.msg"), client, server); //$NON-NLS-1$
    }

}
