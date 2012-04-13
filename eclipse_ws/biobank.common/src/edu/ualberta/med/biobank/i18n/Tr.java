package edu.ualberta.med.biobank.i18n;

import java.util.Arrays;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

class Tr extends AbstractLocalizable {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(Tr.class);

    private final String text;
    private final Object[] objects;

    Tr(String text, Object[] objects) {
        super(text);
        this.text = text;
        this.objects = Arrays.copyOf(objects, objects.length);
    }

    @Override
    String localize() {
        return i18n.tr(text, objects);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = super.hashCode();
        hash = prime * hash + Arrays.hashCode(objects);
        hash = prime * hash + ((text == null) ? 0 : text.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Tr other = (Tr) obj;
        if (!Arrays.equals(objects, other.objects)) return false;
        if (text == null) {
            if (other.text != null) return false;
        } else if (!text.equals(other.text)) return false;
        return true;
    }
}
