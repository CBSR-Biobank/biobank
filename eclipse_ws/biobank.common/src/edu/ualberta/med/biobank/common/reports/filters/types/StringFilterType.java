package edu.ualberta.med.biobank.common.reports.filters.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class StringFilterType implements FilterType {
    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {

        switch (op) {
        case IS_NOT_SET:
            FilterTypeUtil.checkValues(values, 0, 0);
            criteria.add(Restrictions.isNull(aliasedProperty));
            break;
        case MATCHES:
            FilterTypeUtil.checkValues(values, 1, 1);
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.like(aliasedProperty,
                    value.getValue()));
                break;
            }
            break;
        case MATCHES_ANY: {
            FilterTypeUtil.checkValues(values, 1, FilterTypeUtil.NOT_BOUND);
            Disjunction or = Restrictions.disjunction();
            for (ReportFilterValue value : values) {
                or.add(Restrictions.like(aliasedProperty, value.getValue()));
            }
            criteria.add(or);
        }
            break;
        case MATCHES_ALL:
            FilterTypeUtil.checkValues(values, 1, FilterTypeUtil.NOT_BOUND);
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.like(aliasedProperty,
                    value.getValue()));
            }
            break;
        case DOES_NOT_MATCH:
            FilterTypeUtil.checkValues(values, 1, 1);
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.not(Restrictions.like(
                    aliasedProperty, value.getValue())));
                break;
            }
            break;
        case DOES_NOT_MATCH_ANY:
            FilterTypeUtil.checkValues(values, 1, FilterTypeUtil.NOT_BOUND);
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.not(Restrictions.like(
                    aliasedProperty, value.getValue())));
            }
            break;
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.MATCHES,
            FilterOperator.MATCHES_ANY, FilterOperator.MATCHES_ALL,
            FilterOperator.IS_NOT_SET, FilterOperator.DOES_NOT_MATCH,
            FilterOperator.DOES_NOT_MATCH_ANY);
    }
}