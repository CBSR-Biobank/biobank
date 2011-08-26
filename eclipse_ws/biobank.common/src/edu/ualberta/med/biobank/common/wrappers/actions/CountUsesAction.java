package edu.ualberta.med.biobank.common.wrappers.actions;

import java.text.MessageFormat;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Count the number of times the model object of a {@link ModelWrapper} is used
 * by a specific {@link Property}.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public class CountUsesAction<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String COUNT_HQL = "SELECT count(m) FROM {0} m WHERE m.{1} = ?";

    private final Property<? super E, ?> property;
    private final Class<?> propertyClass;

    /**
     * 
     * @param wrapper to get the model object from
     * @param property the {@link Property} of another object that references
     *            the {@link ModelWrapper}'s model object
     */
    public <T> CountUsesAction(ModelWrapper<E> wrapper,
        Property<? super E, ? super T> property, Class<T> propertyClass) {
        super(wrapper);
        this.propertyClass = propertyClass;
        this.property = property;
    }

    @Override
    public Long doAction(Session session) throws BiobankSessionException {
        String hql = MessageFormat.format(COUNT_HQL, propertyClass.getName(),
            property.getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, getModel());

        Long count = HibernateUtil.getCountFromQuery(query);
        return count;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }
}
