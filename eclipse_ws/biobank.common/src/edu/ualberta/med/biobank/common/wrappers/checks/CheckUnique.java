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

    /**
     * Checks that the {@code Collection} of {@code Property}-s is unique for
     * the model object in the {@code ModelWrapper}, excluding the instance
     * itself (if it is already persisted).
     * 
     * @param wrapper which holds the model object
     * @param properties to ensure uniqueness on
     */
    public CheckUnique(ModelWrapper<E> wrapper,
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
            criteria.add(Restrictions.not(Restrictions.eq(idProperty.getName(),
                id)));
        }

        for (Property<?, ? super E> property : properties) {
            // TODO: this is a problem if the associations are not initialized.
            // Perhaps do a post-check through HQL? but a pre-check may be
            // necessary if it is a database constraint. .: properties are okay
            // with a pre-check. Associations with a post-check.
            String name = property.getName();
            Object value = property.get(model);
            criteria.add(Restrictions.eq(name, value));
        }

        List<?> results = criteria.list();
        Long count = CheckUtil.getCountFromResult(results);

        if (count == null || count > 0) {
            String modelClass = Format.modelClass(getModelClass());
            String values = Format.propertyValues(model, properties);
            String names = Format.propertyNames(properties);

            // TODO: I don't think this message makes sense, it reports the
            // existing duplicate as itself. Not very informative :-(
            String msg = MessageFormat.format(EXCEPTION_STRING, modelClass,
                modelString, values, names);

            throw new DuplicatePropertySetException(msg);
        }

        return null;
    }
}