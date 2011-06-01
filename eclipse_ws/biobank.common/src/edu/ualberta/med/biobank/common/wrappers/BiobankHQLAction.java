package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.query.hql.SearchHQLQuery;

import java.util.Arrays;
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

    public BiobankHQLAction(String hql, Object... parameters) {
        super(hql, Arrays.asList(parameters));
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        Query query = session.createQuery(getHqlString());

        int i = 0;
        for (Object param : getParameters()) {
            query.setParameter(i, param);
            i++;
        }

        List<?> results = query.list();
        return doResults(results);
    }

    /**
     * This method is called with the results of the HQL String with given
     * parameters.
     * 
     * @param results
     * @return whatever you want.
     * @throws BiobankSessionException
     */
    public abstract Object doResults(List<?> results)
        throws BiobankSessionException;
}
