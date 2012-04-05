package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.HasLocalizedMessage;
import edu.ualberta.med.biobank.i18n.Msg;

/**
 * 
 * @author Jonathan Ferland
 */
public class ActionException extends RuntimeException
    implements HasLocalizedMessage {
    private static final long serialVersionUID = 1L;

    private final Msg msg;

    public ActionException(Msg msg) {
        this(msg, null);
    }

    public ActionException(Msg msg, Throwable cause) {
        super(msg.getMsg(), cause);

        this.msg = msg;
    }

    @Override
    public String getLocalizedMessage() {
        return msg.getMsg();
    }
}
