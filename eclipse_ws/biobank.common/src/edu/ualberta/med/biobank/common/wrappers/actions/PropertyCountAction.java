package edu.ualberta.med.biobank.common.wrappers.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Counts the number of model objects (of type {@link E}) that have same value
 * for the given {@link Property}-s of the given model object,
 * <em>including</em> the given model object. <em>The model object (wrapped by
 * the given {@link ModelWrapper}) must be saved
 * (persistent)</em>.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public class PropertyCountAction<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String PROPERTIES_EMPTY_ERRMSG = "No properties were given to count. At least one property is required.";
    private static final String HQL = "SELECT COUNT(*) FROM {0} o WHERE ({1}) = (SELECT {2} FROM {0} o2 WHERE o2 = ?)";

    private final Collection<Property<?, ? super E>> properties;

    /**
     * 
     * @param wrapper {@link ModelWrapper} which holds the model object
     * @param properties to ensure uniqueness on
     */
    public PropertyCountAction(ModelWrapper<E> wrapper,
        Collection<Property<?, ? super E>> properties) {
        super(wrapper);

        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException(PROPERTIES_EMPTY_ERRMSG);
        }

        this.properties = properties;
    }

    @Override
    public Long doAction(Session session) throws BiobankSessionException {
        String modelClassName = getModelClass().getName();

        List<String> propertyNames = getPropertyNames(properties);
        String hqlProperties = StringUtil.join(propertyNames, ", ");

        List<String> subPropertyNames = getPropertyNames("o2.", properties);
        String subHqlProperties = StringUtil.join(subPropertyNames, ", ");

        String hql = MessageFormat.format(HQL, modelClassName, hqlProperties,
            subHqlProperties);

        Query query = session.createQuery(hql);
        query.setParameter(0, getModel());
        Long count = HibernateUtil.getCountFromQuery(query);

        return count;
    }

    private List<String> getPropertyNames(
        Collection<Property<?, ? super E>> properties) {
        return getPropertyNames("", properties);
    }

    private List<String> getPropertyNames(String prefix,
        Collection<Property<?, ? super E>> properties) {
        List<String> names = new ArrayList<String>();

        for (Property<?, ?> property : properties) {
            names.add(prefix + property.getName());
        }

        return names;
    }
}