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

    @Override
    public int hashCode() {
        final int prime = 31;
        int i = 1;
        i = prime * i + ((bundleName == null) ? 0 : bundleName.hashCode());
        i = prime * i + ((key == null) ? 0 : key.hashCode());
        return i;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        if (getClass() != that.getClass()) return false;
        BundleL10nedMessage other = (BundleL10nedMessage) that;
        if (bundleName == null) {
            if (other.bundleName != null) return false;
        } else if (!bundleName.equals(other.bundleName)) return false;
        if (key == null) {
            if (other.key != null) return false;
        } else if (!key.equals(other.key)) return false;
        return true;
    }
}
