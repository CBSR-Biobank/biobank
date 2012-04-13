package edu.ualberta.med.biobank.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.xnap.commons.i18n.I18n;

/**
 * A localized string that returns a value according to the current
 * {@link Locale} (as defined by {@link I18n#getLocale()}.
 * 
 * @author Jonathan Ferland
 */
public class LocalizedString implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AbstractLocalizable localizable;
    private transient String string;

    private LocalizedString(AbstractLocalizable localizable) {
        this.localizable = localizable;
        this.string = localizable.localize();
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

    public List<String> getKey() {
        return localizable.getKey();
    }

    public boolean equalsKey(LocalizedString that) {
        return localizable.getKey().equals(that.localizable.getKey());
    }

    @Override
    public String toString() {
        return string;
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

    private void readObject(ObjectInputStream ois)
        throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        string = localizable.localize();
    }
}
