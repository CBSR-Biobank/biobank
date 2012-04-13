package edu.ualberta.med.biobank.i18n;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

class Trc extends AbstractLocalizable {
    private static final long serialVersionUID = 1L;
    private static final I18n i18n = I18nFactory.getI18n(Trc.class);

    private final String context;
    private final String text;

    Trc(String context, String text) {
        super(context, text);
        this.context = context;
        this.text = text;
    }

    @Override
    public String getString() {
        return i18n.trc(context, text);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = super.hashCode();
        hash = prime * hash + ((context == null) ? 0 : context.hashCode());
        hash = prime * hash + ((text == null) ? 0 : text.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Trc other = (Trc) obj;
        if (context == null) {
            if (other.context != null) return false;
        } else if (!context.equals(other.context)) return false;
        if (text == null) {
            if (other.text != null) return false;
        } else if (!text.equals(other.text)) return false;
        return true;
    }
}
