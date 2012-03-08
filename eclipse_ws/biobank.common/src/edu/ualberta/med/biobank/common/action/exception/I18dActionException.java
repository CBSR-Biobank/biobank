package edu.ualberta.med.biobank.common.action.exception;

import edu.ualberta.med.biobank.i18n.I18dMessage;
import edu.ualberta.med.biobank.i18n.OgnlI18dMessage;

public class I18dActionException extends ActionException implements I18dMessage {
    private static final long serialVersionUID = 1L;

    private final I18dMessage delegate;

    public I18dActionException(I18dMessage delegate) {
        super(delegate.getI18dMessage());
        this.delegate = delegate;
    }

    public I18dActionException(I18dMessage delegate, Throwable cause) {
        super(delegate.getI18dMessage(), cause);
        this.delegate = delegate;
    }

    public I18dActionException(I18dMessage delegate, Object root) {
        this(new OgnlI18dMessage(delegate, root));
    }

    @Override
    public String getI18dMessage() {
        return delegate.getI18dMessage();
    }
}
