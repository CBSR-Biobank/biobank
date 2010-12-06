package edu.ualberta.med.biobank.common.reports.filters.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class NumberFilterType implements FilterType {
    private static Double getNumber(ReportFilterValue value) {
        return Double.parseDouble(value.getValue());
    }

    private static Double getSecondNumber(ReportFilterValue value) {
        return Double.parseDouble(value.getSecondValue());
    }

    private static Criterion between(String property, ReportFilterValue value) {
        return Restrictions.between(property, getNumber(value),
            getSecondNumber(value));
    }

    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {

        FilterTypeUtil.checkValues(values, 1, FilterTypeUtil.NOT_BOUND);

        switch (op) {
        case BETWEEN: {
            FilterTypeUtil.checkValues(values, 1, 1);
            for (ReportFilterValue value : values) {
                criteria.add(between(aliasedProperty, value));
                break;
            }
        }
            break;
        case BETWEEN_ANY: {
            Disjunction or = Restrictions.disjunction();
            for (ReportFilterValue value : values) {
                or.add(between(aliasedProperty, value));
            }
            criteria.add(or);
        }
        case NOT_BETWEEN:
            FilterTypeUtil.checkValues(values, 1, 1);
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.not(between(aliasedProperty, value)));
                break;
            }
            break;
        case NOT_BETWEEN_ANY:
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.not(between(aliasedProperty, value)));
            }
            break;
        case LESS_THAN:
            FilterTypeUtil.checkValues(values, 1, 1);
            criteria.add(Restrictions.lt(aliasedProperty,
                getNumber(values.get(0))));
            break;
        case LESS_THAN_OR_EQUAL_TO:
            FilterTypeUtil.checkValues(values, 1, 1);
            criteria.add(Restrictions.le(aliasedProperty,
                getNumber(values.get(0))));
            break;
        case GREATER_THAN:
            FilterTypeUtil.checkValues(values, 1, 1);
            criteria.add(Restrictions.gt(aliasedProperty,
                getNumber(values.get(0))));
            break;
        case GREATER_THAN_OR_EQUAL_TO:
            FilterTypeUtil.checkValues(values, 1, 1);
            criteria.add(Restrictions.ge(aliasedProperty,
                getNumber(values.get(0))));
            break;
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.LESS_THAN,
            FilterOperator.LESS_THAN_OR_EQUAL_TO, FilterOperator.GREATER_THAN,
            FilterOperator.GREATER_THAN_OR_EQUAL_TO, FilterOperator.BETWEEN,
            FilterOperator.BETWEEN_ANY, FilterOperator.NOT_BETWEEN,
            FilterOperator.NOT_BETWEEN_ANY);
    }
}