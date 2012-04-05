package edu.ualberta.med.biobank.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A transient localized message.
 * 
 * @author Jonathan Ferland
 */
public class TI18n implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(TI18n.class);

    private final Translator translator;
    private transient String msg;

    private TI18n(Translator translator) {
        this.translator = translator;
        this.msg = translator.translate();
    }

    public static TI18n tr(String text, Object[] objects) {
        return new TI18n(new Tr(text, objects));
    }

    public static TI18n trc(String context, String text) {
        return new TI18n(new Trc(context, text));
    }

    public static TI18n trn(String singular, String plural, long n,
        Object[] objects) {
        return new TI18n(new Trn(singular, plural, n, objects));
    }

    public static TI18n trnc(String context, String singular,
        String plural, long n, Object[] objects) {
        return new TI18n(new Trnc(context, singular, plural, n, objects));
    }

    public String getMsg() {
        return msg;
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
