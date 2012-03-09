package edu.ualberta.med.biobank.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class AbstractLazyL10nedMessage implements L10nedMessage {
    private static final long serialVersionUID = 1L;

    private transient String message;

    public AbstractLazyL10nedMessage() {
    }

    @Override
    public String getL10nedMessage() {
        if (message == null) message = loadMessage();
        return message;
    }

    private void readObject(ObjectInputStream s) throws IOException,
        ClassNotFoundException {
        s.defaultReadObject();
    }

    /**
     * Called whenever the underlying {@link #message} should be lazily loaded,
     * i.e. when {@link #getL10nedMessage()} is called and the message does not
     * exist: after creation and deserialisation.
     * 
     * @return a message appropriate for
     *         {@link L10nedMessage#getL10nedMessage()}.
     */
    protected abstract String loadMessage();
}
