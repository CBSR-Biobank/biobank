package edu.ualberta.med.biobank.common.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocalizableMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String bundleName;
    private final String key;
    private final Object[] arguments;
    private transient String toString; // reformatted per environment

    /**
     * 
     * @param bundleName
     * @param key
     * @param arguments
     * @throws MissingResourceException if the bundleName or key cannot be found
     * @throws IllegalArgumentException if the found pattern or arguments are
     *             invalid or do not match
     */
    LocalizableMessage(String bundleName, String key, Object... arguments)
        throws MissingResourceException, IllegalArgumentException {
        this.bundleName = bundleName;
        this.key = key;
        this.arguments = arguments;

        format();
    }

    @Override
    public String toString() {
        return toString;
    }

    private void readObject(ObjectInputStream inputStream)
        throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();

        format();
    }

    private void format() {
        // TODO: is it expensive to call getBundle once per message? if so, we
        // should create our own factory.
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
        String pattern = bundle.getString(key);
        String message = MessageFormat.format(pattern, arguments);

        toString = message;
    }
}
