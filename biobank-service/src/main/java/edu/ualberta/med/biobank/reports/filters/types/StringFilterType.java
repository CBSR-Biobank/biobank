package edu.ualberta.med.biobank.reports.filters.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.reports.ReportsUtil;
import edu.ualberta.med.biobank.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class StringFilterType implements FilterType {
    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {

        switch (op) {
        case IS_SET: {
            FilterTypeUtil.checkValues(values, 0, 0);
            criteria.add(Restrictions.isNotNull(aliasedProperty));
        }
            break;
        case IS_NOT_SET: {
            FilterTypeUtil.checkValues(values, 0, 0);
            criteria.add(ReportsUtil.isNotSet(aliasedProperty));
        }
            break;
        case MATCHES:
            FilterTypeUtil.checkValues(values, 0, 1);
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.like(aliasedProperty,
                    value.getValue()));
                break;
            }
            break;
        case MATCHES_ANY: {
            Disjunction or = Restrictions.disjunction();
            for (ReportFilterValue value : values) {
                or.add(Restrictions.like(aliasedProperty, value.getValue()));
            }
            criteria.add(or);
        }
            break;
        case MATCHES_ALL:
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.like(aliasedProperty,
                    value.getValue()));
            }
            break;
        case DOES_NOT_MATCH: {
            FilterTypeUtil.checkValues(values, 0, 1);
            Disjunction or = ReportsUtil.idIsNullOr(aliasedProperty);
            for (ReportFilterValue value : values) {
                or.add(Restrictions.not(Restrictions.like(aliasedProperty,
                    value.getValue())));
                break;
            }
            criteria.add(or);
        }
            break;
        case DOES_NOT_MATCH_ANY: {
            Disjunction or = ReportsUtil.idIsNullOr(aliasedProperty);
            Conjunction and = Restrictions.conjunction();
            for (ReportFilterValue value : values) {
                and.add(Restrictions.not(Restrictions.like(aliasedProperty,
                    value.getValue())));
            }
            or.add(and);
            criteria.add(or);
        }
            break;
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.MATCHES,
            FilterOperator.MATCHES_ANY, FilterOperator.MATCHES_ALL,
            FilterOperator.IS_SET, FilterOperator.IS_NOT_SET,
            FilterOperator.DOES_NOT_MATCH, FilterOperator.DOES_NOT_MATCH_ANY);
    }
}