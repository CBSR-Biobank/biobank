package edu.ualberta.med.biobank.i18n;

class BundleL10nedMessage extends AbstractLazyL10nedMessage {
    private static final long serialVersionUID = 1L;
    private static final ResourceBundleLocator LOCATOR =
        new CachedResourceBundleLocator();

    private final String bundleName;
    private final String key;

    BundleL10nedMessage(String bundleName, String key) {
        this.bundleName = bundleName;
        this.key = key;
    }

    @Override
    protected String loadMessage() {
        return LOCATOR.getResourceBundle(bundleName).getString(key);
    }
}
