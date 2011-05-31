package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.Session;

public class Cascade {
    public static <W1 extends ModelWrapper<M1>, M1, M2> TaskList delete(
        W1 wrapper, Property<M2, M1> property) {
        TaskList tasks = new TaskList();

        if (property.isCollection()) {
            @SuppressWarnings("unchecked")
            Property<? extends Collection<M2>, ? super M1> tmp = (Property<? extends Collection<M2>, ? super M1>) property;
            Collection<ModelWrapper<M2>> list = wrapper.getWrapperCollection(
                tmp, null, false);
            for (ModelWrapper<?> wrappedProperty : list) {
                tasks.add(wrappedProperty.getDeleteTasks());
            }
        } else {
            ModelWrapper<M2> wrapperProperty = wrapper.getWrappedProperty(
                property, null);
            tasks.add(wrapperProperty.getDeleteTasks());
        }

        return tasks;
    }

    public static <W1 extends ModelWrapper<M1>, M1, M2> TaskList persist(
        W1 wrapper, Property<M2, M1> property) {
        TaskList tasks = new TaskList();

        // persist if the property is initialized since it will be sent to the
        // server anyways.
        if (wrapper.isInitialized(property)) {
            if (property.isCollection()) {
                @SuppressWarnings("unchecked")
                Property<? extends Collection<M2>, ? super M1> tmp = (Property<? extends Collection<M2>, ? super M1>) property;
                Collection<ModelWrapper<M2>> list = wrapper
                    .getWrapperCollection(tmp, null, false);
                for (ModelWrapper<?> wrappedProperty : list) {
                    tasks.add(wrappedProperty.getPersistTasks());
                }
            } else {
                ModelWrapper<M2> wrapperProperty = wrapper.getWrappedProperty(
                    property, null);
                tasks.add(wrapperProperty.getPersistTasks());
            }
        }

        return tasks;
    }

    /**
     * IMPORTANT: when old values are deleted using this method, the checks
     * defined in the corresponding wrapper object will NOT be applied. The
     * objects will simply be removed.
     * 
     * @param wrapper
     * @param property
     * @return
     */
    public static <W1 extends ModelWrapper<M1>, M1, M2> TaskList deleteOld(
        W1 wrapper, Property<Collection<M2>, M1> property) {
        TaskList tasks = new TaskList();

        if (!wrapper.isNew() && wrapper.isInitialized(property)) {
            tasks.add(new DeleteRemoved<M1>(wrapper, property));
        }

        return tasks;
    }

    private static class DeleteRemoved<E> extends BiobankSearchAction<E> {
        private static final long serialVersionUID = 1L;

        private final Property<?, ? super E> property;

        protected DeleteRemoved(ModelWrapper<E> wrapper,
            Property<?, ? super E> property) {
            super(wrapper);
            this.property = property;
        }

        @Override
        public Object doAction(Session session)
            throws BiobankSessionActionException {
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
