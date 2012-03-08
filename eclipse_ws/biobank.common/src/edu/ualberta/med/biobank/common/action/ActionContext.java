package edu.ualberta.med.biobank.common.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.util.DiffSet;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

// TODO: for now extend SessionUtil only because I eventually want to delete SessionUtil and replace it with this context :-)
public class ActionContext {
    private final User user;
    private final Session session;
    private BiobankApplicationService appService;

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

        if (id != null && result == null) {
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

    /**
     * The same as {@link #load(Class, Set)} but throws a
     * {@link ModelNotFoundException} if any object in the given
     * {@link DiffSet#getAdditions()} set does not exist. If objects in the
     * {@link DiffSet#getRemovals()} are missing, they are simply ignored since
     * they should not affect the difference.
     * 
     * @param klazz
     * @param ids
     * @return
     * @throws ModelNotFoundException
     */
    public <K extends Serializable, V> DiffSet<V> load(Class<V> klazz,
        DiffSet<K> ids) throws ModelNotFoundException {
        Set<V> additions = new HashSet<V>(ids.getAdditions().size());
        Set<V> removals = new HashSet<V>(ids.getRemovals().size());

        for (K id : ids.getAdditions()) {
            V result = get(klazz, id);

            if (result == null) {
                throw new ModelNotFoundException(klazz, id);
            }

            additions.add(result);
        }

        for (K id : ids.getRemovals()) {
            V result = get(klazz, id);
            removals.add(result);
        }

        return DiffSet.copy(additions, removals);
    }

    public WritableApplicationService getAppService() {
        return appService;
    }
}
