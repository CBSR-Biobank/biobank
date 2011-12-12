package edu.ualberta.med.biobank.common.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class OldI18nMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String bundleName;
    private final String key;
    private final Object[] objects;
    private transient String toString;

    public OldI18nMessage(String bundleName, String key, Object... objects) {
        this.bundleName = bundleName;
        this.key = key;
        this.objects = objects;

        generateToString();
    }

    @Override
    public String toString() {
        return toString;
    }

    private void readObject(ObjectInputStream inputStream)
        throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();

        generateToString();
    }

    private void generateToString() {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName);

        String pattern = bundle.getString(key);
        String toString = MessageFormat.format(pattern, objects);

        this.toString = toString;
    }
}
