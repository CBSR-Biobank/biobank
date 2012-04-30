package edu.ualberta.med.biobank.common.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public enum Messages implements IFormattable {
    Greeting("greeting"),
    Super("super");

    private static final String BUNDLE_NAME =
        Messages.class.getName().toLowerCase();
    private static final String VALUE_NOT_FOUND =
        "Cannot find value for {0}.{1} with key {2} in bundle {3}.";

    static {
        listUnreferencedProperties();
    }

    private final String string;

    private Messages(String key) {
        this.string = getString(key);
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public String format(Object... objects) {
        String result = MessageFormat.format(string, objects);
        return result;
    }

    private String getString(String key) {
        try {
            String baseName = Messages.class.getName().toLowerCase();
            ResourceBundle bundle = ResourceBundle.getBundle(baseName);
            return bundle.getString(key);
        } catch (MissingResourceException caught) {
            String message = MessageFormat.format(VALUE_NOT_FOUND,
                getClass().getName(), name(), key, BUNDLE_NAME);

            throw new RuntimeException(message, caught);
        }
    }

    private static void listUnreferencedProperties() {
    }

    public static void main(String[] args) {
        System.out.println(Messages.class.getName().toLowerCase());
        System.out.println("BUNDLE_NAME=" + BUNDLE_NAME);
    }
}
