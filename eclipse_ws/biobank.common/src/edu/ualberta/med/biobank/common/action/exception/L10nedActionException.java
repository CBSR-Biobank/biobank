package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.HasL10nedMessage;
import edu.ualberta.med.biobank.i18n.L10nedMessage;
import edu.ualberta.med.biobank.i18n.OgnlL10nedMessage;

public class L10nedActionException extends ActionException
    implements HasL10nedMessage {
    private static final long serialVersionUID = 1L;

    private final L10nedMessage message;

    public L10nedActionException(L10nedMessage message) {
        super(message.getMessage());
        this.message = message;
    }

    public L10nedActionException(L10nedMessage message, Throwable cause) {
        super(message.getMessage(), cause);
        this.message = message;
    }

    @Override
    public L10nedMessage getL10nedMessage() {
        return message;
    }

    public static L10nedActionException ognl(L10nedMessage message, Object root) {
        return new L10nedActionException(new OgnlL10nedMessage(message, root));
    }
}
