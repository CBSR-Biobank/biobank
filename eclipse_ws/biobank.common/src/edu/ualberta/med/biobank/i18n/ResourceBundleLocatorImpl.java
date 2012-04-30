package edu.ualberta.med.biobank.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleLocatorImpl implements ResourceBundleLocator {
    private Locale defaultLocale;

    public ResourceBundleLocatorImpl() {
        defaultLocale = Locale.getDefault();
    }

    @Override
    public ResourceBundle getResourceBundle(String bundleName) {
        return getResourceBundle(bundleName, defaultLocale);
    }

    @Override
    public ResourceBundle getResourceBundle(String bundleName, Locale locale) {
        return ResourceBundle.getBundle(bundleName, locale);
    }
}
