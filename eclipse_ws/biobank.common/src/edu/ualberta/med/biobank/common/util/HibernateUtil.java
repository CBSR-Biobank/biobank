package edu.ualberta.med.biobank.common.util;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class HibernateUtil {
    public static Long getCountFromResult(List<?> results) {
        Long count = 0L;
        if (results != null && results.size() == 1
            && (results.get(0) instanceof Number)) {
            count = Long.valueOf(((Number) results.get(0)).longValue());
        }
        return count;
    }

    public static Long getCountFromCriteria(Criteria criteria) {
        List<?> results = criteria.list();
        return getCountFromResult(results);
    }

    public static Long getCountFromQuery(Query query) {
        try {
            List<?> results = query.list();
            return getCountFromResult(results);
        } catch (HibernateException he) {
            throw he;
        }
    }
}
