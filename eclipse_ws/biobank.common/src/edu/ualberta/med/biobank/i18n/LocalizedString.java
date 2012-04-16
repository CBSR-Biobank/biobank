package edu.ualberta.med.biobank.i18n;

import java.util.List;
import java.util.Locale;

import org.xnap.commons.i18n.I18n;

/**
 * A localized string that returns a value according to the current
 * {@link Locale}.
 * 
 * @author Jonathan Ferland
 */
public class LocalizedString extends LazyString {
    private static final long serialVersionUID = 1L;

    private final AbstractLocalizable localizable;

    private LocalizedString(AbstractLocalizable localizable) {
        this.localizable = localizable;
    }

    /**
     * Wrapper for {@link I18n#tr(String, Object[])}.
     * 
     * @param text
     * @param objects
     * @return
     */
    public static LocalizedString tr(String text, Object... objects) {
        return new LocalizedString(new Tr(text, objects));
    }

    /**
     * Wrapper for {@link I18n#trc(String, String)}.
     * 
     * @param text
     * @param objects
     * @return
     */
    public static LocalizedString trc(String context, String text) {
        return new LocalizedString(new Trc(context, text));
    }

    /**
     * Wrapper for {@link I18n#trn(String, String, long, Object[])}.
     * 
     * @param text
     * @param objects
     * @return
     */
    public static LocalizedString trn(String singular, String plural, long n,
        Object... objects) {
        return new LocalizedString(new Trn(singular, plural, n, objects));
    }

    /**
     * Wrapper for {@link I18n#trnc(String, String, String, long, Object[])}.
     * 
     * @param context
     * @param singular
     * @param plural
     * @param n
     * @param objects
     * @return
     */
    public static LocalizedString trnc(String context, String singular,
        String plural, long n, Object... objects) {
        return new LocalizedString(new Trnc(context, singular, plural, n,
            objects));
    }

    public static LocalizedString lit(String literal) {
        return new LocalizedString(new Literal(literal));
    }

    /**
     * Returns a {@link Locale}-independent key for the underlying string
     * (useful for seeing if the strings have the same template, but not
     * necessarily the same variable values).
     * 
     * @return
     */
    public List<String> getKey() {
        return localizable.getKey();
    }

    @Override
    public int hashCode() {
        int hash = 31 + ((localizable == null) ? 0 : localizable.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LocalizedString other = (LocalizedString) obj;
        if (localizable == null) {
            if (other.localizable != null) return false;
        } else if (!localizable.equals(other.localizable)) return false;
        return true;
    }

    @Override
    protected String loadString() {
        return localizable.getString();
    }
}
