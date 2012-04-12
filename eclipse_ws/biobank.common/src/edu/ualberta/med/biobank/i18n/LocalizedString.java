package edu.ualberta.med.biobank.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A localized string that returns a value according to the current
 * {@link Locale} (as defined by {@link I18n#getLocale()}.
 * 
 * @author Jonathan Ferland
 */
public class LocalizedString implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(LocalizedString.class);

    private final Translator translator;
    private transient String msg;

    private LocalizedString(Translator translator) {
        this.translator = translator;
        this.msg = translator.translate();
    }

    public static LocalizedString tr(String text, Object... objects) {
        return new LocalizedString(new Tr(text, objects));
    }

    public static LocalizedString trc(String context, String text) {
        return new LocalizedString(new Trc(context, text));
    }

    public static LocalizedString trn(String singular, String plural, long n,
        Object... objects) {
        return new LocalizedString(new Trn(singular, plural, n, objects));
    }

    public static LocalizedString trnc(String context, String singular,
        String plural, long n, Object... objects) {
        return new LocalizedString(new Trnc(context, singular, plural, n,
            objects));
    }

    @Override
    public String toString() {
        return msg;
    }

    private void readObject(ObjectInputStream ois)
        throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        msg = translator.translate();
    }

    private interface Translator extends Serializable {
        public String translate();
    }

    private static class Tr implements Translator {
        private static final long serialVersionUID = 1L;

        private final String text;
        private final Object[] objects;

        private Tr(String text, Object[] objects) {
            this.text = text;
            this.objects = Arrays.copyOf(objects, objects.length);
        }

        @Override
        public String translate() {
            return i18n.tr(text, objects);
        }
    }

    public static class Trc implements Translator {
        private static final long serialVersionUID = 1L;

        private final String context;
        private final String text;

        private Trc(String context, String text) {
            this.context = context;
            this.text = text;
        }

        @Override
        public String translate() {
            return i18n.trc(context, text);
        }
    }

    public static class Trn implements Translator {
        private static final long serialVersionUID = 1L;

        private final String singular;
        private final String plural;
        private final long n;
        private final Object[] objects;

        private Trn(String singular, String plural, long n, Object[] objects) {
            this.singular = singular;
            this.plural = plural;
            this.n = n;
            this.objects = objects;
        }

        @Override
        public String translate() {
            return i18n.trn(singular, plural, n, objects);
        }
    }

    public static class Trnc implements Translator {
        private static final long serialVersionUID = 1L;

        private final String context;
        private final String singular;
        private final String plural;
        private final long n;
        private final Object[] objects;

        private Trnc(String context, String singular, String plural, long n,
            Object[] objects) {
            this.context = context;
            this.singular = singular;
            this.plural = plural;
            this.n = n;
            this.objects = objects;
        }

        @Override
        public String translate() {
            return i18n.trnc(context, singular, plural, n, objects);
        }
    }
}
