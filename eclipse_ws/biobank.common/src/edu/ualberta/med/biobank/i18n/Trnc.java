package edu.ualberta.med.biobank.i18n;

import java.util.Arrays;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

class Trnc extends AbstractLocalizable {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(Trnc.class);

    private final String context;
    private final String singular;
    private final String plural;
    private final long n;
    private final Object[] objects;

    Trnc(String context, String singular, String plural, long n,
        Object[] objects) {
        super(context, singular, plural);
        this.context = context;
        this.singular = singular;
        this.plural = plural;
        this.n = n;
        this.objects = objects;
    }

    @Override
    String localize() {
        return i18n.trnc(context, singular, plural, n, objects);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = super.hashCode();
        hash = prime * hash + ((context == null) ? 0 : context.hashCode());
        hash = prime * hash + (int) (n ^ (n >>> 32));
        hash = prime * hash + Arrays.hashCode(objects);
        hash = prime * hash + ((plural == null) ? 0 : plural.hashCode());
        hash = prime * hash + ((singular == null) ? 0 : singular.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Trnc other = (Trnc) obj;
        if (context == null) {
            if (other.context != null) return false;
        } else if (!context.equals(other.context)) return false;
        if (n != other.n) return false;
        if (!Arrays.equals(objects, other.objects)) return false;
        if (plural == null) {
            if (other.plural != null) return false;
        } else if (!plural.equals(other.plural)) return false;
        if (singular == null) {
            if (other.singular != null) return false;
        } else if (!singular.equals(other.singular)) return false;
        return true;
    }
}
