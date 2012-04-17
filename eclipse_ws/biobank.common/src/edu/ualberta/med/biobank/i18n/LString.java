package edu.ualberta.med.biobank.i18n;

import java.io.Serializable;

public abstract class LString implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LTemplate template;

    LString(LTemplate template) {
        this.template = template;
    }

    public LTemplate getTemplate() {
        return template;
    }

    @Deprecated
    public static LString lit(final String literal) {
        return new LString(null) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getString() {
                return literal;
            }
        };
    }

    public static LString tr(String text, Object... objects) {
        return LTemplate.tr(text).format(objects);
    }

    public static LString trn(String text, String pluralText, int n,
        Object... objects) {
        return LTemplate.trn(text, pluralText).format(n, objects);
    }

    public static LString trc(String context, String text) {
        return LTemplate.trc(context, text).format();
    }

    public static LString trnc(String context, String text, String pluralText,
        int n, Object... objects) {
        return LTemplate.trnc(context, text, pluralText).format(n, objects);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime + ((template == null) ? 0 : template.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LString other = (LString) obj;
        if (template == null) {
            if (other.template != null) return false;
        } else if (!template.equals(other.template)) return false;
        return true;
    }

    @Override
    public String toString() {
        return getString();
    }

    public abstract String getString();
}
