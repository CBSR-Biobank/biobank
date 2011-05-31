package edu.ualberta.med.biobank.common.wrappers.checks;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.wrappers.BiobankSearchAction;
import edu.ualberta.med.biobank.common.wrappers.BiobankSessionActionException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;

public class CheckUnique<E> extends BiobankSearchAction<E> {
    private static final long serialVersionUID = 1L;

    private final Property<? extends Serializable, ? super E> idProperty;
    private final Collection<Property<?, E>> properties;

    protected CheckUnique(ModelWrapper<E> modelWrapper,
        Collection<Property<?, E>> properties) {
        super(modelWrapper);
        this.idProperty = modelWrapper.getIdProperty();
        this.properties = properties;
    }

    @Override
    public Object doAction(Session session)
        throws BiobankSessionActionException {
        Criteria criteria = session.createCriteria(getModelClass());

        criteria.setProjection(Projections.rowCount());

        E model = getModel();

        Serializable id = idProperty.get(model);
        if (id != null) {
            // reassociate the model object with the given Session
            // TODO: reassociate associations?
            // session.lock(model, LockMode.NONE);

            criteria.add(Restrictions.not(Restrictions.eq(idProperty.getName(),
                id)));
        }

        for (Property<?, E> property : properties) {
            String name = property.getName();
            Object value = property.get(model);

            criteria.add(Restrictions.eq(name, value));
        }

        List<?> results = criteria.list();

        // TODO: more descriptive error messages! i.e. include information about
        // the searched object!
        if (results == null || results.isEmpty()) {
            throw new BiobankSessionActionException("Missing information.");
        } else if (results.size() > 1) {
            throw new BiobankSessionActionException("Extra information.");
        } else if (!(results.get(0) instanceof Number)) {
            throw new BiobankSessionActionException("Unexpected information.");
        } else if (((Number) results.get(0)).longValue() > 0) {
            throw new BiobankSessionActionException("Already exists.");
        }

        return null;
    }
}