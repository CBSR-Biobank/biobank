package edu.ualberta.med.biobank.reports.filters.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.reports.ReportsUtil;
import edu.ualberta.med.biobank.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.report.ReportFilterValue;

public abstract class NumberFilterType<E extends Number> implements FilterType {
    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {

        switch (op) {
        case EQUALS:
            FilterTypeUtil.checkValues(values, 0, 1);
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.eq(aliasedProperty,
                    getFirstNumber(value)));
                break;
            }
            break;
        case DOES_NOT_EQUAL: {
            FilterTypeUtil.checkValues(values, 0, 1);
            Disjunction or = ReportsUtil.idIsNullOr(aliasedProperty);
            for (ReportFilterValue value : values) {
                or.add(Restrictions.ne(aliasedProperty, getFirstNumber(value)));
                break;
            }
            criteria.add(or);
        }
            break;
        case IS_IN: {
            Disjunction or = Restrictions.disjunction();
            for (ReportFilterValue value : values) {
                or.add(Restrictions.eq(aliasedProperty, getFirstNumber(value)));
            }
            criteria.add(or);
        }
            break;
        case IS_NOT_IN: {
            Disjunction or = ReportsUtil.idIsNullOr(aliasedProperty);
            Conjunction and = Restrictions.conjunction();
            for (ReportFilterValue value : values) {
                and.add(Restrictions.ne(aliasedProperty, getFirstNumber(value)));
            }
            or.add(and);
            criteria.add(or);
        }
            break;
        case IS_NOT_SET:
            FilterTypeUtil.checkValues(values, 0, 0);
            criteria.add(ReportsUtil.isNotSet(aliasedProperty));
            break;
        case BETWEEN: {
            FilterTypeUtil.checkValues(values, 0, 1);
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
            break;
        case NOT_BETWEEN: {
            FilterTypeUtil.checkValues(values, 0, 1);
            Disjunction or = ReportsUtil.idIsNullOr(aliasedProperty);
            for (ReportFilterValue value : values) {
                or.add(Restrictions.not(between(aliasedProperty, value)));
                break;
            }
            criteria.add(or);
        }
            break;
        case NOT_BETWEEN_ANY: {
            Disjunction or = ReportsUtil.idIsNullOr(aliasedProperty);
            Conjunction and = Restrictions.conjunction();
            for (ReportFilterValue value : values) {
                and.add(Restrictions.not(between(aliasedProperty, value)));
            }
            or.add(and);
            criteria.add(or);
        }
            break;
        case LESS_THAN:
            FilterTypeUtil.checkValues(values, 0, 1);
            criteria.add(Restrictions.lt(aliasedProperty,
                getFirstNumber(values.get(0))));
            break;
        case LESS_THAN_OR_EQUAL_TO:
            FilterTypeUtil.checkValues(values, 0, 1);
            criteria.add(Restrictions.le(aliasedProperty,
                getFirstNumber(values.get(0))));
            break;
        case GREATER_THAN:
            FilterTypeUtil.checkValues(values, 0, 1);
            criteria.add(Restrictions.gt(aliasedProperty,
                getFirstNumber(values.get(0))));
            break;
        case GREATER_THAN_OR_EQUAL_TO:
            FilterTypeUtil.checkValues(values, 0, 1);
            criteria.add(Restrictions.ge(aliasedProperty,
                getFirstNumber(values.get(0))));
            break;
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.EQUALS,
            FilterOperator.DOES_NOT_EQUAL, FilterOperator.IS_IN,
            FilterOperator.IS_NOT_IN, FilterOperator.LESS_THAN,
            FilterOperator.LESS_THAN_OR_EQUAL_TO, FilterOperator.GREATER_THAN,
            FilterOperator.GREATER_THAN_OR_EQUAL_TO, FilterOperator.IS_NOT_SET,
            FilterOperator.BETWEEN, FilterOperator.BETWEEN_ANY,
            FilterOperator.NOT_BETWEEN, FilterOperator.NOT_BETWEEN_ANY);
    }

    protected abstract E getNumber(String string);

    private E getFirstNumber(ReportFilterValue value) {
        return getNumber(value.getValue());
    }

    private E getSecondNumber(ReportFilterValue value) {
        return getNumber(value.getSecondValue());
    }

    private Criterion between(String property, ReportFilterValue value) {
        return Restrictions.between(property, getFirstNumber(value),
            getSecondNumber(value));
    }
}