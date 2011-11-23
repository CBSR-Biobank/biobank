package edu.ualberta.med.biobank.common.action.check;

import java.text.MessageFormat;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.CountResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.User;

/**
 * Count the number of times an object is referenced/ used by a given
 * {@link Property}.
 * 
 * @author delphine
 * @author jferland
 * 
 * @param <E>
 */
public class UsageCountAction implements Action<CountResult> {
    private static final long serialVersionUID = 1L;
    private static final String COUNT_HQL =
        "SELECT count(m) FROM {0} m WHERE m.{1} = ?"; //$NON-NLS-1$
    private final Property<?, ?> property;
    private Object model;

    public <E> UsageCountAction(E model, Property<? super E, ?> property) {
        this.model = model;
        this.property = property;
    }

    @Override
    public CountResult run(User user, Session session) throws ActionException {
        String modelClass = property.getModelClass().getName();
        String name = property.getName();
        String hql = MessageFormat.format(COUNT_HQL, modelClass, name);

        Query query = session.createQuery(hql);
        query.setParameter(0, model);

        Long count = HibernateUtil.getCountFromQuery(query);
        return new CountResult(count);
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    public Property<?, ?> getProperty() {
        return property;
    }
}
