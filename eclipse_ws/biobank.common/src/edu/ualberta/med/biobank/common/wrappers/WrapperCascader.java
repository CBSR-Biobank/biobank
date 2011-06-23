package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.actions.BiobankWrapperAction;
import edu.ualberta.med.biobank.common.wrappers.tasks.ClearCollectionQueryTask;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Convenience class for defining cascade-type methods to be performed.
 * 
 * @author jferland
 * 
 * @param <E> wrapped object
 */
public class WrapperCascader<E> {
    private final ModelWrapper<E> wrapper;

    /**
     * @param wrapper the {@link ModelWrapper} to perform the cascade methods on
     */
    WrapperCascader(ModelWrapper<E> wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * Convenience method to put the delete {@link TaskList} of all of the given
     * {@link ModelWrapper}-s into a single {@link TaskList} and return it.
     * <p>
     * A {@link ClearCollectionQueryTask} is added on the end of the
     * {@link TaskList} to clear the given {@link Collection} if and when all
     * elements ({@link ModelWrapper}-s) have successfully been deleted.
     * <p>
     * Internal tracking of elements is very easy to get wrong! Simply adding to
     * an element tracking list when adding elements and removing from a list
     * when removing is wrong
     * 
     * @param wrappers
     * @return
     */
    public <T> TaskList delete(Collection<? extends ModelWrapper<T>> wrappers) {
        TaskList tasks = new TaskList();

        for (ModelWrapper<T> wrapper : wrappers) {
            tasks.add(wrapper.getDeleteTasks());
        }

        tasks.add(new ClearCollectionQueryTask(wrapper, wrappers));

        return tasks;
    }

    /**
     * Convenience method to put the persist {@link TaskList} of all of the
     * given {@link ModelWrapper}-s into a single {@link TaskList} and return
     * it.
     * <p>
     * A {@link ClearCollectionQueryTask} is added on the end of the
     * {@link TaskList} to clear the given {@link Collection} if and when all
     * elements ({@link ModelWrapper}-s) have successfully been persisted.
     * 
     * @param wrappers
     * @return
     */
    public <T> TaskList persist(Collection<? extends ModelWrapper<T>> wrappers) {
        TaskList tasks = new TaskList();

        for (ModelWrapper<T> wrapper : wrappers) {
            tasks.add(wrapper.getPersistTasks());
        }

        tasks.add(new ClearCollectionQueryTask(wrapper, wrappers));

        return tasks;
    }

    /**
     * Gets the information to delete a {@link Property} by building a new
     * {@link TaskList} from its (or each of its elements') {@link
     * getDeleteTasks()} method(s). This is done whether or not the
     * {@link Property} has been loaded, for obvious reasons, so will cost extra
     * time to get the information from the database.
     * <p>
     * This method is <em>potentially network expensive</em> since it may
     * require information to be lazily loaded from the database when called.
     * Consider using {@link deleteRemovedUnchecked()} if checks and persists
     * are unnecessary.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public <T> TaskList delete(Property<T, E> property) {
        TaskList tasks = new TaskList();

        if (property.isCollection()) {
            @SuppressWarnings("unchecked")
            Property<? extends Collection<T>, ? super E> tmp = (Property<? extends Collection<T>, ? super E>) property;
            Collection<ModelWrapper<T>> list = wrapper.getWrapperCollection(
                tmp, null, false);
            for (ModelWrapper<?> wrappedProperty : list) {
                tasks.add(wrappedProperty.getDeleteTasks());
            }
        } else {
            ModelWrapper<T> wrapperProperty = wrapper.getWrappedProperty(
                property, null);
            if (wrapperProperty != null) {
                tasks.add(wrapperProperty.getDeleteTasks());
            }
        }

        return tasks;
    }

    /**
     * Gets the information to persist a {@link Property} by building a new
     * {@link TaskList} from its (or each of its elements') {@link
     * getPersistTasks()} method(s). Note that this is only done if the given
     * {@link Property} has been set (including if it has only been loaded).
     * Whether it has been modified or not is not taken into account.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public <T> TaskList persist(Property<T, E> property) {
        TaskList tasks = new TaskList();

        // If the property is initialized (in the wrapped object) but not in the
        // wrapper, then persist it anyways since the data will be sent anyways
        // and a Hibernate cascade may take place and we may as well be aware of
        // it.
        if (wrapper.isPropertyCached(property)
            || wrapper.isInitialized(property)) {
            if (property.isCollection()) {
                @SuppressWarnings("unchecked")
                Property<? extends Collection<T>, ? super E> tmp = (Property<? extends Collection<T>, ? super E>) property;
                Collection<ModelWrapper<T>> list = wrapper
                    .getWrapperCollection(tmp, null, false);
                for (ModelWrapper<?> wrappedProperty : list) {
                    tasks.add(wrappedProperty.getPersistTasks());
                }
            } else {
                ModelWrapper<T> wrapperProperty = wrapper.getWrappedProperty(
                    property, null);
                if (wrapperProperty != null) {
                    tasks.add(wrapperProperty.getPersistTasks());
                }
            }
        }

        return tasks;
    }

    /**
     * Deletes removed elements from the given {@link Property} for a
     * collection.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public <T> TaskList deleteRemoved(
        Property<? extends Collection<T>, E> property) {
        TaskList tasks = new TaskList();

        Collection<ModelWrapper<T>> removed = wrapper.getElementTracker()
            .getRemovedElements(property);
        for (ModelWrapper<T> wrapper : removed) {
            tasks.add(wrapper.getDeleteTasks());
        }

        return tasks;
    }

    /**
     * Persists removed elements from the given {@link Property} for a
     * collection.
     * 
     * @param <T>
     * @param property
     * @return
     */
    public <T> TaskList persistRemoved(
        Property<? extends Collection<T>, E> property) {
        TaskList tasks = new TaskList();

        Collection<ModelWrapper<T>> removed = wrapper.getElementTracker()
            .getRemovedElements(property);
        for (ModelWrapper<T> wrapper : removed) {
            tasks.add(wrapper.getPersistTasks());
        }

        return tasks;
    }

    /**
     * Persists all newly added elements in the collection for the given
     * {@link Property}.
     * <p>
     * This is actually <em>dangerous</em> because if a property is added and
     * modified then <em>all</em> changes will be persisted, not just it being
     * added to the collection (e.g. if the name was changed, the name change
     * will be persisted as well).
     * 
     * @param <T>
     * @param property
     * @return
     */
    public <T> TaskList persistAdded(
        Property<? extends Collection<T>, E> property) {
        TaskList tasks = new TaskList();

        Collection<ModelWrapper<T>> added = wrapper.getElementTracker()
            .getAddedElements(property);
        for (ModelWrapper<T> wrapper : added) {
            tasks.add(wrapper.getPersistTasks());
        }

        return tasks;
    }

    /**
     * Deletes old values of the given property (that do not exist in the new
     * set of values) WITHOUT PERFORMING CHECKS (specifically, getDeleteTasks()
     * will not be called).
     * <p>
     * When old values are deleted using this method, the <em>checks
     * defined in the corresponding wrapper object will NOT be applied</em>. The
     * objects will simply be removed. Further, cascades defined in the wrappers
     * will NOT be applied.
     * 
     * @param property
     * @return
     */
    public <T> TaskList deleteRemovedUnchecked(Property<T, E> property) {
        TaskList tasks = new TaskList();

        if (!wrapper.isNew() && wrapper.isInitialized(property)) {
            tasks.add(new DeleteRemovedUnchecked<E>(wrapper, property));
        }

        return tasks;
    }

    private static class DeleteRemovedUnchecked<E> extends
        BiobankWrapperAction<E> {
        private static final long serialVersionUID = 1L;

        private final Property<?, ? super E> property;

        protected DeleteRemovedUnchecked(ModelWrapper<E> wrapper,
            Property<?, ? super E> property) {
            super(wrapper);
            this.property = property;
        }

        @Override
        public Object doAction(Session session) throws BiobankSessionException {
            Collection<Object> newValues = getNewValues();
            Collection<Object> oldValues = getOldValues(session);
            Collection<Object> removedValues = new ArrayList<Object>(oldValues);
            removedValues.removeAll(newValues);

            for (Object removedValue : removedValues) {
                session.delete(removedValue);
            }

            // TODO: try turning off session caching instead?
            // need to flush and clear the session so that specific entities do
            // not linger and cause exceptions to be thrown. For example, if a
            // model is persisted then deleted in a single transaction, it is
            // possible that an object will be double-deleted (first by this
            // method, removing an old value, then by cascading a delete on the
            // old value), throwing an exception.
            session.flush();
            session.clear();

            return null;
        }

        private Collection<Object> getNewValues() {
            Collection<Object> newValues = new ArrayList<Object>();

            E model = getModel();
            Object propertyValue = property.get(model);

            if (propertyValue instanceof Collection) {
                newValues.addAll((Collection<?>) propertyValue);
            } else {
                newValues.add(propertyValue);
            }

            return newValues;
        }

        private Collection<Object> getOldValues(Session session) {
            session = session.getSession(EntityMode.POJO);

            Collection<Object> oldValues = new ArrayList<Object>();

            String hql = "SELECT " + property.getName() + " FROM "
                + getModelClass().getName() + " m WHERE m = ?";
            Query query = session.createQuery(hql);
            query.setParameter(0, getModel());

            List<?> results = query.list();
            oldValues.addAll(results);

            return oldValues;
        }
    }
}
