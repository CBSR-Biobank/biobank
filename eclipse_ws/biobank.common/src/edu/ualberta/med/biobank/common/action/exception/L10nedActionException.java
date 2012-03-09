package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.L10nedMessage;
import edu.ualberta.med.biobank.i18n.OgnlL10nedMessage;

public class L10nedActionException extends ActionException
    implements L10nedMessage {
    private static final long serialVersionUID = 1L;

    private final L10nedMessage delegate;

    public L10nedActionException(L10nedMessage delegate) {
        super(delegate.getL10nedMessage());
        this.delegate = delegate;
    }

    public L10nedActionException(L10nedMessage delegate, Throwable cause) {
        super(delegate.getL10nedMessage(), cause);
        this.delegate = delegate;
    }

    @Override
    public String getL10nedMessage() {
        return delegate.getL10nedMessage();
    }

    public static L10nedActionException ognl(L10nedMessage message, Object root) {
        return new L10nedActionException(new OgnlL10nedMessage(message, root));
    }
}
