package edu.ualberta.med.biobank.i18n;

import java.io.Serializable;

/**
 * Holds a localized {@link String}, accessible through {@link #getString} (or
 * {@link #toString()}), returned in the current locale.
 * 
 * @author Jonathan Ferland
 */
public abstract class LString implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Template template;

    LString(Template template) {
        this.template = template;
    }

    public Template getTemplate() {
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
