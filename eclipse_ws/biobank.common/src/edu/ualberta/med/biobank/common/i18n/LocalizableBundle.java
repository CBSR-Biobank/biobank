package edu.ualberta.med.biobank.common.i18n;

public class LocalizableBundle {
    private final String bundleName;

    public LocalizableBundle(String bundleName) {
        this.bundleName = bundleName;
    }

    protected LocalizablePattern getPattern(String key) {
        return null;
    }
}
