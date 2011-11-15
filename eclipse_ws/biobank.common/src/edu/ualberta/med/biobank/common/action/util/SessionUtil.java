package edu.ualberta.med.biobank.common.action.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;

public class SessionUtil {
    private final Session session;

    public SessionUtil(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public <E> E get(Class<E> klazz, Serializable id) {
        if (id == null) return null;

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

    /**
     * The same as {@link #get(Class, Serializable)}, but throws a
     * {@link ModelNotFoundException} if no object exists with the given id.
     * 
     * @param klazz
     * @param id
     * @return
     * @throws ModelNotFoundException
     */
    public <E> E load(Class<E> klazz, Serializable id)
        throws ModelNotFoundException {
        E result = get(klazz, id);

        if (result == null) {
            throw new ModelNotFoundException(klazz, id);
        }

        return result;
    }

    /**
     * The same as {@link #get(Class, Serializable, Object)}, but throws a
     * {@link ModelNotFoundException} if any object in the given set of ids does
     * not exist
     * 
     * @param klazz
     * @param ids
     * @return
     * @throws ModelNotFoundException
     */
    public <K extends Serializable, V> Map<K, V> load(Class<V> klazz, Set<K> ids)
        throws ModelNotFoundException {
        Map<K, V> results = new HashMap<K, V>();

        for (K id : ids) {
            V result = get(klazz, id);

            if (result == null) {
                throw new ModelNotFoundException(klazz, id);
            }

            results.put(id, result);
        }

        return results;
    }
}
