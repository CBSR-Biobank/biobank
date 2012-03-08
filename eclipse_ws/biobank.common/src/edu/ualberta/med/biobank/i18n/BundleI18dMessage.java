package edu.ualberta.med.biobank.i18n;

public class BundleI18dMessage extends AbstractI18dMessage {
    private static final long serialVersionUID = 1L;
    private static final ResourceBundleLocator LOCATOR =
        new CachedResourceBundleLocator();

    private final String bundleName;
    private final String key;

    public BundleI18dMessage(String bundleName, String key) {
        this.bundleName = bundleName;
        this.key = key;
    }

    @Override
    protected String loadMessage() {
        return LOCATOR.getResourceBundle(bundleName).getString(key);
    }
}
