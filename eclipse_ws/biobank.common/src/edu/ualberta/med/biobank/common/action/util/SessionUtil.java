package edu.ualberta.med.biobank.common.action.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

public class SessionUtil {
    public static <E> E load(Session session, Class<E> klazz, Serializable id) {
        @SuppressWarnings("unchecked")
        E result = (E) session.load(klazz, id);
        return result;
    }

    public static <E> Map<Serializable, E> load(Session session,
        Class<E> klazz, Set<Serializable> ids) {
        Map<Serializable, E> results = new HashMap<Serializable, E>();

        for (Serializable id : ids) {
            E result = load(session, klazz, id);
            results.put(id, result);
        }

        return results;
    }
}
