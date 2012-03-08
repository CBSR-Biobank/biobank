package edu.ualberta.med.biobank.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class AbstractI18dMessage implements I18dMessage {
    private static final long serialVersionUID = 1L;

    private transient String message;

    public AbstractI18dMessage() {
        this.message = loadMessage();
    }

    @Override
    public String getI18dMessage() {
        return message;
    }

    private void readObject(ObjectInputStream s) throws IOException,
        ClassNotFoundException {
        s.defaultReadObject();
        this.message = loadMessage();
    }

    /**
     * Called whenever the underlying {@link #message} should be re-loaded, i.e.
     * upon creation and deserialisation.
     * 
     * @return a message appropriate for {@link I18dMessage#getI18dMessage()}.
     */
    protected abstract String loadMessage();
}
