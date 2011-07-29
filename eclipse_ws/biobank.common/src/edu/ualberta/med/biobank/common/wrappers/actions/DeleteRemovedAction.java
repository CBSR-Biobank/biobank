package edu.ualberta.med.biobank.common.wrappers.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Deletes old values of the given property (that do not exist in the new set of
 * values) WITHOUT PERFORMING CHECKS (specifically, getDeleteTasks() will not be
 * called).
 * <p>
 * When old values are deleted using this method, the <em>checks
 * defined in the corresponding wrapper object will NOT be applied</em>. The
 * objects will simply be removed. Further, cascades defined in the wrappers
 * will NOT be applied.
 * 
 * @author jferland
 * 
 */
// TODO: consider never using this because the old values are probably always
// loaded (see ModelWrapper.setModelProperty()).
public class DeleteRemovedAction<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;

    private final Property<?, ? super E> property;

    public DeleteRemovedAction(ModelWrapper<E> wrapper,
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
        // session.flush();
        // session.clear();

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
        Collection<Object> oldValues = new ArrayList<Object>();

        String hql = "SELECT " + property.getName() + " FROM "
            + getModelClass().getName() + " m WHERE m = ?";
        Query query = session.createQuery(hql);
        query.setCacheable(false);
        query.setParameter(0, getModel());

        List<?> results = query.list();
        oldValues.addAll(results);

        return oldValues;
    }
}