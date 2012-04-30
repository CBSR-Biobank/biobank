package edu.ualberta.med.biobank.common.action.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.property.GetterInterceptor;

/**
 * If a property is not initialised, then run an HQL query to get it from the
 * database. Useful when {@link Property}-s are used with detached model
 * objects.
 * 
 * @author jferland
 * 
 */
public class HqlInterceptor implements GetterInterceptor, Serializable {
    private static final long serialVersionUID = 1L;

    private static final String HQL = "SELECT o.{0} FROM {1} o WHERE o = ?";

    private final Session session;
    private final Integer maxMemoryDepth;
    private Integer depth = 1;

    /**
     * Always use an HQL query to get the database value of a property after the
     * given number of calls.
     * 
     * @param session
     * @param maxMemoryDepth the maximum calls to make in memory before forcing
     *            HQL to be used and getting the value from the database.
     */
    private HqlInterceptor(Session session, Integer maxMemoryDepth) {
        this.session = session;
        this.maxMemoryDepth = maxMemoryDepth;
    }

    @Override
    public <P, M> P get(Property<P, M> subProperty, M model) {
        P value = null;
        String propertyName = subProperty.getName();

        if (isLegalDepth()
            && Hibernate.isPropertyInitialized(model, propertyName)) {
            value = subProperty.get(model);
        } else {
            String modelName = subProperty.getModelClass().getName();
            String hql = MessageFormat.format(HQL, propertyName, modelName);

            Query query = session.createQuery(hql);
            query.setCacheable(false); // don't interfere with cache
            query.setParameter(0, model);

            List<?> results = query.list();

            if (subProperty.isCollection()) {
                @SuppressWarnings("unchecked")
                P tmp = (P) results;
                value = tmp;
            } else {
                @SuppressWarnings("unchecked")
                P tmp = (P) results.get(0);
                value = tmp;
            }
        }

        depth++;

        return value;
    }

    private boolean isLegalDepth() {
        return maxMemoryDepth == null || depth <= maxMemoryDepth;
    }

    public static <P, M> P get(Session session, M model,
        Property<P, ? super M> property, int depth) {
        return property.get(model, new HqlInterceptor(session, depth));
    }
}
