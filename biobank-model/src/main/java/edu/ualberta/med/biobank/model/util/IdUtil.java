package edu.ualberta.med.biobank.model.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.model.HasId;

public class IdUtil {
    public static <E extends Serializable> E getId(HasId<E> o) {
        return o != null ? o.getId() : null;
    }

    public static <E extends Serializable> Set<E> getIds(
        Set<? extends HasId<E>> c) {
        Set<E> ids = new HashSet<E>();
        for (HasId<E> element : c) {
            ids.add(element.getId());
        }
        return ids;
    }
}
