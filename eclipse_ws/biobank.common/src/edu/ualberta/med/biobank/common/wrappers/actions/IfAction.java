package edu.ualberta.med.biobank.common.wrappers.actions;

import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Decorates a {@link BiobankSessionAction}. Perform a
 * {@link BiobankSessionAction} if the wrapped object's {@link Property} is
 * <em>something</em> ({@link Is}).
 * <p>
 * This class expects that the wrapped object <em>is saved</em> to work
 * properly.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public class IfAction<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String HQL = "SELECT COUNT(*) FROM {0} m {1} WHERE m = ? {2}";

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
     * 
     * @param wrapper
     * @param property should NOT be an association. If an association is
     *            wanted, then end with the id property of that association
     *            (e.g. specimenPosition.id NOT specimenPosition).
     * @param is (e.g. should the {@link Property} be null or not null)
     * @param action what to do if the condition is true
     */
    public IfAction(ModelWrapper<E> wrapper,
        Property<?, ? super E> property, Is is, BiobankSessionAction action) {
        super(wrapper);
        this.property = property;
        this.is = is;
        this.action = action;
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        StringBuilder joins = new StringBuilder();
        StringBuilder conditions = new StringBuilder();

        // use left joins in case the property is allowed to be null.
        List<String> names = property.getNames();
        String joinPoint = "m";
        for (int i = 0, n = names.size() - 1; i < n; i++) {
            joins.append(" LEFT JOIN ");
            joins.append(joinPoint);
            joins.append(".");
            joins.append(names.get(i));
            joins.append(" p");
            joins.append(i);

            conditions.append(" AND p");
            conditions.append(i);
            conditions.append(" ");
            conditions.append(is.hqlString);

            joinPoint = "p" + Integer.toString(i);
        }

        String hql = MessageFormat.format(HQL, getModelClass().getName(),
            joins.toString(), conditions.toString());
        Query query = session.createQuery(hql);
        query.setParameter(0, getModel());

        List<?> results = query.list();
        Long count = (Long) results.get(0);

        if (count >= 1) {
            return action.doAction(session);
        }

        return null;
    }
}
