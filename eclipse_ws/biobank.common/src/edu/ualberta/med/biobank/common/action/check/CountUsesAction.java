package edu.ualberta.med.biobank.common.action.check;

import java.text.MessageFormat;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.IBiobankModel;
import edu.ualberta.med.biobank.model.User;

/**
 * Count the number of times the model object of a {@link IBiobankModel} is used
 * by a specific {@link Property}.
 * 
 * @author delphine
 * 
 * @param <E>
 */
public class CountUsesAction<E extends IBiobankModel> implements Action<Long> {
    private static final long serialVersionUID = 1L;
    private static final String COUNT_HQL = "SELECT count(m) FROM {0} m WHERE m.{1} = ?"; //$NON-NLS-1$

    private final Property<? super E, ?> linkProperty;
    private final Class<?> linkPropertyClass;
    private E model;

    /**
     * 
     * @param idProperty if of the model object to test
     * @param property the {@link Property} of another object that references
     *            the {@link IBiobankModel}'s model object
     */
    public <T> CountUsesAction(E model,
        Property<? super E, ? super T> linkProperty, Class<T> linkPropertyClass) {
        this.model = model;
        this.linkPropertyClass = linkPropertyClass;
        this.linkProperty = linkProperty;
    }

    @Override
    public Long run(User user, Session session) throws ActionException {
        String hql = MessageFormat.format(COUNT_HQL,
            linkPropertyClass.getName(), linkProperty.getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, model);

        Long count = HibernateUtil.getCountFromQuery(query);
        return count;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    public Class<?> getPropertyClass() {
        return linkPropertyClass;
    }

}
