package edu.ualberta.med.biobank.i18n;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class L10nedException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(L10nedException.class);

    public L10nedException(String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return i18n.tr("");
    }

    public static void test() {
        new L10nedException(i18n.marktr("there was an exception"));
        new L10nedException(i18n.tr("Hello, {0}. You {1}", "Bob", "suck"));
    }
}
