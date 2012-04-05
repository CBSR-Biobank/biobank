package edu.ualberta.med.biobank.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A "Smart String" holder that is aware of i18n local changes.
 * 
 * @author Jonathan Ferland
 */
public class SS implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(SS.class);

    private final Translator translator;
    private transient String msg;

    private SS(Translator translator) {
        this.translator = translator;
        this.msg = translator.translate();
    }

    public static SS tr(String text) {
        return tr(text, new Object[] {});
    }

    public static SS tr(String text, Object[] objects) {
        return new SS(new Tr(text, objects));
    }

    public static SS tr(String text, Object o1) {
        return new SS(new Tr(text, new Object[] { o1 }));
    }

    public static SS tr(String text, Object o1, Object o2) {
        return new SS(new Tr(text, new Object[] { o1, o2 }));
    }

    public static SS tr(String text, Object o1, Object o2, Object o3) {
        return new SS(new Tr(text, new Object[] { o1, o2, o3 }));
    }

    public static SS tr(String text, Object o1, Object o2, Object o3,
        Object o4) {
        return new SS(new Tr(text, new Object[] { o1, o2, o3, o4 }));
    }

    public static SS trc(String context, String text) {
        return new SS(new Trc(context, text));
    }

    public static SS trn(String singular, String plural, long n,
        Object[] objects) {
        return new SS(new Trn(singular, plural, n, objects));
    }

    public static SS trnc(String context, String singular,
        String plural, long n, Object[] objects) {
        return new SS(new Trnc(context, singular, plural, n, objects));
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
