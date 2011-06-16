package edu.ualberta.med.biobank.common.wrappers.actions;

import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.BiobankSessionAction;
import edu.ualberta.med.biobank.common.wrappers.BiobankWrapperAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class IfProperty<E> extends BiobankWrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String NULL_CHECK_HQL = "SELECT m.{0} {1} FROM {2} m WHERE m = ?";

    private final Property<?, ? super E> property;
    private final Is is;
    private final BiobankSessionAction action;

    public enum Is {
        // @formatter:off
        NULL("IS NULL"),
        NOT_NULL("IS NOT NULL");
        // @formatter:on;

        public final String hqlString;

        private Is(String hqlString) {
            this.hqlString = hqlString;
        }
    }

    /**
     * Perform the given {@code BiobankSessionAction} if the wrapped object's
     * {@code Property} is _something_.
     * 
     * @param wrapper
     * @param property
     * @param is
     * @param action
     */
    protected IfProperty(ModelWrapper<E> wrapper,
        Property<?, ? super E> property, Is is, BiobankSessionAction action) {
        super(wrapper);
        this.property = property;
        this.is = is;
        this.action = action;
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        String hql = MessageFormat.format(NULL_CHECK_HQL, property.getName(),
            is.hqlString, getModelClass().getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, getModel());

        List<?> results = query.list();
        Boolean result = (Boolean) results.get(0);

        if (result) {
            return action.doAction(session);
        }

        return null;
    }
}
