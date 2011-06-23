package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;

/**
 * Checks that the {@link Collection} of {@link Property}-s is unique for the
 * model object in the {@link ModelWrapper}, excluding the instance itself (if
 * it is already persisted).
 * <p>
 * This check is <em>intended for direct properties, not associations</em>. To
 * check uniqueness of properties through associations, see
 * {@link UniqueOnSavedCheck}.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public class UniqueCheck<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_STRING = "There already exists a {0} with property value(s) ({1}) for ({2}), respectively. These field(s) must be unique.";

    private final Collection<Property<?, ? super E>> properties;

    /**
     * 
     * @param wrapper {@link ModelWrapper} which holds the model object
     * @param properties to ensure uniqueness on
     */
    public UniqueCheck(ModelWrapper<E> wrapper,
        Collection<Property<?, ? super E>> properties) {
        super(wrapper);
        this.properties = properties;
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        Criteria criteria = getCriteria(session);
        Long count = HibernateUtil.getCountFromCriteria(criteria);

        if (count > 0) {
            throwException();
        }

        return null;
    }

    private void throwException() throws DuplicatePropertySetException {
        String modelClass = Format.modelClass(getModelClass());
        String values = Format.propertyValues(getModel(), properties);
        String names = Format.propertyNames(properties);

        String msg = MessageFormat.format(EXCEPTION_STRING, modelClass, values,
            names);

        throw new DuplicatePropertySetException(msg);
    }

    private Criteria getCriteria(Session session) {
        Criteria criteria = session.createCriteria(getModelClass());

        criteria.setProjection(Projections.rowCount());

        E model = getModel();

        Integer id = getModelId();
        if (id != null) {
            String idName = getIdProperty().getName();
            criteria.add(Restrictions.not(Restrictions.eq(idName, id)));
        }

        for (Property<?, ? super E> property : properties) {
            String name = property.getName();
            Object value = property.get(model);
            criteria.add(Restrictions.eq(name, value));
        }

        return criteria;
    }
}