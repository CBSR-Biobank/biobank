package edu.ualberta.med.biobank.common.reports.filters.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;

public class StringFilterType implements FilterType {
    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty, FilterOperator op,
        List<String> values) {
        switch (op) {
        case MATCHES: {
            Disjunction or = Restrictions.disjunction();
            for (String value : values) {
                or.add(Restrictions.like(aliasedProperty, value));
            }
            criteria.add(or);
        }
            break;
        case DOES_NOT_MATCH:
            for (String value : values) {
                criteria.add(Restrictions.not(Restrictions.like(aliasedProperty, value)));
            }
            break;
        case MATCHES_ALL:
            for (String value : values) {
                criteria.add(Restrictions.like(aliasedProperty, value));
            }
            break;
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.MATCHES,
            FilterOperator.DOES_NOT_MATCH, FilterOperator.MATCHES_ALL);
    }
}