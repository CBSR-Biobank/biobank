package edu.ualberta.med.biobank.i18n;

import java.io.Serializable;

public abstract class LazyString implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient String string;

    public final String getString() {
        if (string == null) string = loadString();
        return string;
    }

    protected abstract String loadString();

    protected void reloadString() {
        string = loadString();
    }

    @Override
    public String toString() {
        return getString();
    }
}
