package edu.ualberta.med.biobank.i18n;

import java.util.Arrays;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public abstract class I18nedException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(I18nedException.class);

    private I18nedException(String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return i18n.tr("");
    }

    public static I18nedException tr() {
        return null;
    }

    protected abstract String translate();

    public class Tr {
        private final String message;
        private final Object[] objects;

        private Tr(String message, Object[] objects) {
            this.message = message;
            this.objects = Arrays.copyOf(objects, objects.length);
        }
    }
}
