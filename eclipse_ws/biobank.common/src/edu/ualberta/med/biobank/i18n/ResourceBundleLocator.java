package edu.ualberta.med.biobank.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleLocator {
    /**
     * Same as {@link #getResourceBundle(String, Locale)} but uses
     * {@link Locale#getDefault()}.
     * 
     * @param bundleName
     * @return
     */
    public ResourceBundle getResourceBundle(String bundleName);

    public ResourceBundle getResourceBundle(String bundleName, Locale locale);
}
