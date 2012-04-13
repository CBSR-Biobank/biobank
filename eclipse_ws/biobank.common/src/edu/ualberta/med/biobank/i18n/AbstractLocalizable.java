package edu.ualberta.med.biobank.i18n;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractLocalizable implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<String> key;

    public abstract String getString();

    protected AbstractLocalizable(String key1, String... keyn) {
        List<String> tmp = new ArrayList<String>(keyn.length + 1);
        tmp.add(key1);
        tmp.addAll(Arrays.asList(keyn));

        this.key = Collections.unmodifiableList(tmp);
    }

    List<String> getKey() {
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
        AbstractLocalizable other = (AbstractLocalizable) obj;
        if (key == null) {
            if (other.key != null) return false;
        } else if (!key.equals(other.key)) return false;
        return true;
    }
}
