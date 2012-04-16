package edu.ualberta.med.biobank.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public abstract class LString {
    private static final I18n i18n = I18nFactory.getI18n(LString.class);

    private final List<String> key;

    protected LString(String key1, String... keyn) {
        List<String> tmp = new ArrayList<String>(keyn.length + 1);
        tmp.add(key1);
        tmp.addAll(Arrays.asList(keyn));

        this.key = Collections.unmodifiableList(tmp);
    }

    public static Tr tr(String text) {
        return new Tr(text);
    }

    public static Trn trn(String text, String pluralText) {
        return new Trn(text, pluralText);
    }

    public static Trc trc(String context, String text) {
        return new Trc(context, text);
    }

    public static Trnc trnc(String context, String text, String pluralText) {
        return new Trnc(context, text, pluralText);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    public static void test() {
        final LString.Tr MSG1 = LString.tr("Hello, {0}. You are {1}!");

        System.out.println(MSG1.format("Jon", "awesome"));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LString other = (LString) obj;
        if (key == null) {
            if (other.key != null) return false;
        } else if (!key.equals(other.key)) return false;
        return true;
    }

    public static class Tr extends LString {
        private final String text;

        public Tr(String text) {
            super(text);
            this.text = text;
        }

        public String format(Object... objects) {
            return i18n.tr(text, objects);
        }
    }

    public static class Trn extends LString {
        private final String text;
        private final String pluralText;

        public Trn(String text, String pluralText) {
            super(text, pluralText);
            this.text = text;
            this.pluralText = pluralText;
        }

        public String format(int n, Object... objects) {
            return i18n.trn(text, pluralText, n, objects);
        }
    }

    public static class Trc extends LString {
        private final String context;
        private final String text;

        public Trc(String context, String text) {
            super(context, text);
            this.context = context;
            this.text = text;
        }

        public String format() {
            return i18n.trc(context, text);
        }
    }

    public static class Trnc extends LString {
        private final String context;
        private final String text;
        private final String pluralText;

        public Trnc(String context, String text, String pluralText) {
            super(context, text, pluralText);
            this.context = context;
            this.text = text;
            this.pluralText = pluralText;
        }

        public String format(int n, Object... objects) {
            return i18n.trnc(context, text, pluralText, n, objects);
        }
    }
}
