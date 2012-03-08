package edu.ualberta.med.biobank.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class CachedResourceBundleLocator extends ResourceBundleLocatorImpl {
    private static final Map<String, ResourceBundle> CACHE = null;

    @Override
    public ResourceBundle getResourceBundle(String bundleName, Locale locale) {
        ResourceBundle bundle = CACHE.get(bundleName);
        if (bundle == null) {
            bundle = super.getResourceBundle(bundleName, locale);
            CACHE.put(bundleName, bundle);
        }
        return bundle;
    }
}
