package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.MembershipPeer;
import edu.ualberta.med.biobank.common.peer.PrincipalPeer;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.UncachedAction;
import edu.ualberta.med.biobank.common.wrappers.property.GetterInterceptor;
import edu.ualberta.med.biobank.common.wrappers.property.LazyLoaderInterceptor;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;

/**
 * Checks that the {@link Collection} of {@link Property}-s is unique for the
 * model object in the {@link ModelWrapper}, excluding the instance itself (if
 * it is already persisted).
 * 
 * @author delphine
 * 
 */
public class MembershipUniquePreCheck extends UncachedAction<Membership> {
    private static final long serialVersionUID = 1L;
    private static final String HQL_START = "SELECT COUNT(*) FROM " //$NON-NLS-1$
        + Membership.class.getName() + " WHERE"; //$NON-NLS-1$
    private static final String EXCEPTION_STRING = Messages
        .getString("MembershipUniquePreCheck.exception.string"); //$NON-NLS-1$

    /**
     * 
     * @param wrapper {@link ModelWrapper} which holds the model object
     * @param properties to ensure uniqueness on
     */
    public MembershipUniquePreCheck(ModelWrapper<Membership> wrapper) {
        super(wrapper);
    }

    @Override
    public void doUncachedAction(Session session)
        throws BiobankSessionException {
        Query query = getQuery(session);

        Long count = HibernateUtil.getCountFromQuery(query);

        if (count > 0) {
            throwException();
        }
    }

    private void throwException() throws DuplicatePropertySetException {
        String modelClass = Format.modelClass(getModelClass());
        Collection<Property<?, ? super Membership>> properties =
            new ArrayList<Property<?, ? super Membership>>();
        properties.add(MembershipPeer.PRINCIPAL);
        // properties.add(MembershipPeer.CENTER);
        // properties.add(MembershipPeer.STUDY);
        String values = Format.propertyValues(getModel(), properties);
        String names = Format.propertyNames(properties);

        String msg = MessageFormat.format(EXCEPTION_STRING, modelClass, values,
            names);

        throw new DuplicatePropertySetException(msg);
    }

    private Query getQuery(Session session) {
        StringBuffer hql = new StringBuffer(HQL_START);
        appendValueTest(hql, session,
            MembershipPeer.PRINCIPAL.to(PrincipalPeer.ID));
        hql.append(" and "); //$NON-NLS-1$
        // appendValueTest(hql, session,
        // MembershipPeer.CENTER.to(CenterPeer.ID));
        hql.append(" and "); //$NON-NLS-1$
        // appendValueTest(hql, session, MembershipPeer.STUDY.to(StudyPeer.ID));

        hql.append(getNotSelfCondition());

        Query query = session.createQuery(hql.toString());

        return query;
    }

    private String getNotSelfCondition() {
        String idCheck = ""; //$NON-NLS-1$

        Integer id = getModelId();
        if (id != null) {
            String idName = getIdProperty().getName();
            idCheck = " AND " + idName + " <> " + id; //$NON-NLS-1$ //$NON-NLS-2$
        }

        return idCheck;
    }

    private void appendValueTest(StringBuffer sb, Session session,
        Property<?, ? super Membership> property) {
        Membership model = getModel();
        GetterInterceptor lazyLoad = new LazyLoaderInterceptor(session, 1);
        Object value = property.get(model, lazyLoad);
        sb.append(" ").append(property.getName()); //$NON-NLS-1$
        if (value == null)
            sb.append(" is null"); //$NON-NLS-1$
        else
            sb.append(" = '").append(value).append("'"); //$NON-NLS-1$ //$NON-NLS-2$

    }
}