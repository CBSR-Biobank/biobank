package edu.ualberta.med.biobank.common.i18n;

public class LocalizableBundle {
    @SuppressWarnings("unused")
    private final String bundleName;

    public LocalizableBundle(String bundleName) {
        this.bundleName = bundleName;
    }

    @SuppressWarnings("unused")
    protected LocalizablePattern getPattern(String key) {
        return null;
    }
}
