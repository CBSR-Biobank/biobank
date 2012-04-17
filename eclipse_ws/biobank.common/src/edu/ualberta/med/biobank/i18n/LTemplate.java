package edu.ualberta.med.biobank.i18n;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class LTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(LTemplate.class);

    private final List<String> key;

    private LTemplate(String key1, String... keyn) {
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

    public List<String> getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LTemplate other = (LTemplate) obj;
        if (key == null) {
            if (other.key != null) return false;
        } else if (!key.equals(other.key)) return false;
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "LTemplate [key=" + key + "]";
    }

    public static class Tr extends LTemplate {
        private static final long serialVersionUID = 1L;

        private final String text;

        public Tr(String text) {
            super(text);
            this.text = text;
        }

        public LString format(final Object... objects) {
            return new LString(this) {
                private static final long serialVersionUID = 1L;

                @Override
                public String getString() {
                    return i18n.tr(text, objects);
                }
            };
        }
    }

    public static class Trn extends LTemplate {
        private static final long serialVersionUID = 1L;

        private final String text;
        private final String pluralText;

        public Trn(String text, String pluralText) {
            super(text, pluralText);
            this.text = text;
            this.pluralText = pluralText;
        }

        public LString format(final int n, final Object... objects) {
            return new LString(this) {
                private static final long serialVersionUID = 1L;

                @Override
                public String getString() {
                    return i18n.trn(text, pluralText, n, objects);
                }
            };
        }
    }

    public static class Trc extends LTemplate {
        private static final long serialVersionUID = 1L;

        private final String context;
        private final String text;

        public Trc(String context, String text) {
            super(context, text);
            this.context = context;
            this.text = text;
        }

        public LString format() {
            return new LString(this) {
                private static final long serialVersionUID = 1L;

                @Override
                public String getString() {
                    return i18n.trc(context, text);
                }
            };
        }
    }

    public static class Trnc extends LTemplate {
        private static final long serialVersionUID = 1L;

        private final String context;
        private final String text;
        private final String pluralText;

        public Trnc(String context, String text, String pluralText) {
            super(context, text, pluralText);
            this.context = context;
            this.text = text;
            this.pluralText = pluralText;
        }

        public LString format(final int n, final Object... objects) {
            return new LString(this) {
                private static final long serialVersionUID = 1L;

                @Override
                public String getString() {
                    return i18n.trnc(context, text, pluralText, n, objects);
                }
            };
        }
    }
}
