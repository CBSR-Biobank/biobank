package edu.ualberta.med.biobank.common.wrappers.checks;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.wrappers.BiobankWrapperAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;

public class CheckUnique<E> extends BiobankWrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_STRING = "There already exists a {0} ({1}) with property value(s) ({2}) for ({3}), respectively. These field(s) must be unique.";

    private final Property<? extends Serializable, ? super E> idProperty;
    private final Collection<Property<?, ? super E>> properties;
    private final String modelString;

    protected CheckUnique(ModelWrapper<E> wrapper,
        Collection<Property<?, ? super E>> properties) {
        super(wrapper);
        this.idProperty = wrapper.getIdProperty();
        this.properties = properties;
        this.modelString = wrapper.toString();
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
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

        for (Property<?, ? super E> property : properties) {
            String name = property.getName();
            Object value = property.get(model);
            criteria.add(Restrictions.eq(name, value));
        }

        List<?> results = criteria.list();
        Long count = Check.getCountFromResult(results);

        if (count == null || count > 0) {
            String modelClass = Format.modelClass(getModelClass());
            String values = Format.propertyValues(model, properties);
            String names = Format.propertyNames(properties);

            String msg = MessageFormat.format(EXCEPTION_STRING, modelClass,
                modelString, values, names);

            throw new DuplicatePropertySetException(msg);
        }

        return null;
    }
}