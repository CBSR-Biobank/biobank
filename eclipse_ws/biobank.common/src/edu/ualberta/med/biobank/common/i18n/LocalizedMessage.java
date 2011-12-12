package edu.ualberta.med.biobank.common.i18n;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class LocalizedMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private final IFormattable formattable;
    private final Object[] objects;
    private transient String toString; // reformatted per environment

    public LocalizedMessage(IFormattable formattable, Object... objects) {
        this.formattable = formattable;
        this.objects = objects;

        format();
    }

    @Override
    public String toString() {
        return toString;
    }

    private void readObject(ObjectInputStream inputStream)
        throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();

        format();
    }

    private void format() {
        toString = formattable.format(objects);
    }
}
