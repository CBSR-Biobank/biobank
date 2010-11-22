package edu.ualberta.med.biobank.common.reports.filters.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;

public class NumberFilterType implements FilterType {
    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<String> values) {

        if (values.isEmpty()) {
            return;
        }

        switch (op) {
        case BETWEEN: {
            // TODO: decide how to store the mulitple values. Maybe we should
            // just be passing around the FilterValue object instead? (which
            // could have a value and a secondValue field?)
            Disjunction or = Restrictions.disjunction();
            for (String value : values) {
                String[] numbers = value.split(",");
                if (numbers.length == 2) {
                    or.add(Restrictions.between(aliasedProperty, numbers[0],
                        numbers[1]));
                }
            }
            criteria.add(or);
        }
            break;
        case NOT_BETWEEN:
            for (String value : values) {
                String[] numbers = value.split(",");
                if (numbers.length == 2) {
                    criteria.add(Restrictions.not(Restrictions.between(
                        aliasedProperty, numbers[0], numbers[1])));
                }
            }
            break;
        case LESS_THAN:
            criteria.add(Restrictions.lt(aliasedProperty, values.get(0)));
            break;
        case LESS_THAN_OR_EQUAL_TO:
            criteria.add(Restrictions.le(aliasedProperty, values.get(0)));
            break;
        case GREATER_THAN:
            criteria.add(Restrictions.gt(aliasedProperty, values.get(0)));
            break;
        case GREATER_THAN_OR_EQUAL_TO:
            criteria.add(Restrictions.ge(aliasedProperty, values.get(0)));
            break;
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.LESS_THAN,
            FilterOperator.LESS_THAN_OR_EQUAL_TO, FilterOperator.GREATER_THAN,
            FilterOperator.GREATER_THAN_OR_EQUAL_TO);
    }
}