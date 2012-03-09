package edu.ualberta.med.biobank.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class AbstractLazyL10nedMessage implements L10nedMessage {
    private static final long serialVersionUID = 1L;

    private transient String message;

    public AbstractLazyL10nedMessage() {
    }

    @Override
    public String getMessage() {
        if (message == null) message = loadMessage();
        return message;
    }

    private void readObject(ObjectInputStream s) throws IOException,
        ClassNotFoundException {
        s.defaultReadObject();
    }

    /**
     * Called whenever the underlying {@link #message} should be lazily loaded,
     * i.e. when {@link #getMessage()} is called and the message does not exist:
     * after creation and deserialisation.
     * 
     * @return a message appropriate for {@link L10nedMessage#getMessage()}.
     */
    protected abstract String loadMessage();
}
