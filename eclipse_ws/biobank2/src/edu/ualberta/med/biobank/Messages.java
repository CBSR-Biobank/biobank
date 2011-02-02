package edu.ualberta.med.biobank;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
        .getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getFormattedString(String key, Object... args) {
        String pattern = getString(key);
        if (pattern != null && args != null && args.length > 0) {
            return MessageFormat.format(pattern, args);
        }
        return pattern;
    }
}
