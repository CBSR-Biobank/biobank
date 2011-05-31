package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.query.hql.SearchHQLQuery;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Extends {@code SearchHQLQuery} so it can use/ pass through the existing
 * security features. Implements {@code BiobankSessionAction} so this class is
 * executed as a different type of query.
 * 
 * @author jferland
 * 
 */
public abstract class BiobankHQLAction extends SearchHQLQuery implements
    BiobankSessionAction {
    private static final long serialVersionUID = 1L;

    public BiobankHQLAction(String hql, List<Object> parameters) {
        super(hql, parameters);
    }

    @Override
    public Object doAction(Session session)
        throws BiobankSessionActionException {
        Query query = session.createQuery(getHqlString());

        int i = 0;
        for (Object param : getParameters()) {
            query.setParameter(i, param);
            i++;
        }

        // TODO: is this appropriate?
        return null;
    }

    /**
     * This method is called with the results of the HQL String with given
     * parameters.
     * 
     * @param results
     * @throws BiobankSessionActionException
     */
    public abstract void doResults(List<?> results)
        throws BiobankSessionActionException;
}
