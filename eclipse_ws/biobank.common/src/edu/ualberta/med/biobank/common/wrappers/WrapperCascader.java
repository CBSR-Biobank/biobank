package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

class WrapperCascader<E> {
    private final ModelWrapper<E> wrapper;

    WrapperCascader(ModelWrapper<E> wrapper) {
        this.wrapper = wrapper;
    }

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
            tasks.add(wrapperProperty.getDeleteTasks());
        }

        return tasks;
    }

    public <T> TaskList persist(Property<T, E> property) {
        TaskList tasks = new TaskList();

        // persist if the property is initialized since it will be sent to the
        // server anyways.
        // TODO: switch to an "isModified" check, called when setters are
        // called...
        if (wrapper.isPropertyCached(property)) {
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
                tasks.add(wrapperProperty.getPersistTasks());
            }
        }

        return tasks;
    }

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
     * {@code Property}.
     * 
     * NOTE: this is actually dangerous because if a property is added and
     * modified then _ALL_ changes will be peristed, not just it being added to
     * the collection (e.g. if the name was changed, the name change will be
     * persisted as well).
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
     * set of values).
     * 
     * IMPORTANT: when old values are deleted using this method, the checks
     * defined in the corresponding wrapper object will NOT be applied. The
     * objects will simply be removed.
     * 
     * @param property
     * @return
     */
    // TODO: remove this in favour of deleteRemoved()
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
