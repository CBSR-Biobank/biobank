package edu.ualberta.med.biobank.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.model.User;

// TODO: for now extend SessionUtil only because I eventually want to delete SessionUtil and replace it with this context :-)
public class ActionContext {
    private final User user;
    private final Session session;
    private final BiobankApplicationService appService;

    public ActionContext(User user, Session session,
        BiobankApplicationService appService) {
        this.user = user;
        this.session = session;
        this.appService = appService;
    }

    public User getUser() {
        return user;
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
     * {@link ModelNotFoundException} if no object exists with the given id,
     * unless the id is null;
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
     * The same as {@link #load(Class, Serializable)}, but throws a
     * {@link ModelNotFoundException} if no object exists with the given id,
     * unless the given id is null, then the default value is returned.
     * 
     * @param klazz
     * @param id
     * @param defaultValue
     * @return
     * @throws ModelNotFoundException
     */
    public <E> E load(Class<E> klazz, Serializable id, E defaultValue)
        throws ModelNotFoundException {
        E result = get(klazz, id, defaultValue);

        if (id != null && result == defaultValue) {
            throw new ModelNotFoundException(klazz, id);
        }

        return result;
    }

    /**
     * The same as {@link #load(Class, Serializable)}, but done on a {@link Set}
     * of ids and returns a {@link Set} of model objects.
     * 
     * @param klazz
     * @param ids
     * @return
     * @throws ModelNotFoundException
     */
    public <K extends Serializable, V> Set<V> load(Class<V> klazz, Set<K> ids)
        throws ModelNotFoundException {
        Set<V> results = new HashSet<V>();
        for (K id : ids) {
            V result = load(klazz, id);
            results.add(result);
        }
        return results;
    }

    public static <T> T singleResult(Query q, Class<T> klazz, Serializable id)
        throws ModelNotFoundException {
        T result = null;
        try {
            @SuppressWarnings("unchecked")
            T tmp = (T) q.uniqueResult();
            result = tmp;
        } catch (Exception e) {
            throw new ModelNotFoundException(klazz, id, e);
        }

        if (result == null) throw new ModelNotFoundException(klazz, id);

        return result;
    }

    public WritableApplicationService getAppService() {
        return appService;
    }
}
