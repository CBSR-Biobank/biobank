package edu.ualberta.med.biobank.common.util;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;

public class HibernateUtil {
    public static Long getCountFromResult(List<?> results) {
        Long count = new Long(0);
        if (results != null && results.size() == 1
            && (results.get(0) instanceof Number)) {
            count = new Long(((Number) results.get(0)).longValue());
        }
        return count;
    }

    public static Long getCountFromCriteria(Criteria criteria) {
        List<?> results = criteria.list();
        return getCountFromResult(results);
    }

    public static Long getCountFromQuery(Query query) {
        List<?> results = query.list();
        return getCountFromResult(results);
    }
}
