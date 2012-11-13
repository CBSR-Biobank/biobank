package edu.ualberta.med.biobank.i18n;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Template implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final Bundle bundle;
    private final List<String> key;

    Template(Bundle bundle, String key1, String... keyn) {
        this.bundle = bundle;

        List<String> tmp = new ArrayList<String>(keyn.length + 1);
        tmp.add(key1);
        tmp.addAll(Arrays.asList(keyn));

        this.key = Collections.unmodifiableList(tmp);
    }

    public Bundle getBundle() {
        return bundle;
    }

    public List<String> getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bundle == null) ? 0 : bundle.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Template other = (Template) obj;
        if (bundle == null) {
            if (other.bundle != null) return false;
        } else if (!bundle.equals(other.bundle)) return false;
        if (key == null) {
            if (other.key != null) return false;
        } else if (!key.equals(other.key)) return false;
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "Template [bundle=" + bundle + ", key="
            + Arrays.toString(key.toArray()) + "]";
    }
}
