package edu.ualberta.med.biobank.common.action.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

public class SessionUtil {
    private final Session session;

    public SessionUtil(Session session) {
        this.session = session;
    }

    public <E> E get(Class<E> klazz, Serializable id) {
        @SuppressWarnings("unchecked")
        E result = (E) session.get(klazz, id);
        return result;
    }

    public <E> E get(Class<E> klazz, Serializable id, E defaultValue) {
        E result = get(klazz, id);
        return result != null ? result : defaultValue;
    }

    public <K extends Serializable, V> Map<K, V> get(Class<V> klazz, Set<K> ids) {
        Map<K, V> results = new HashMap<K, V>();

        for (K id : ids) {
            V result = get(klazz, id);
            results.put(id, result);
        }

        return results;
    }
}
