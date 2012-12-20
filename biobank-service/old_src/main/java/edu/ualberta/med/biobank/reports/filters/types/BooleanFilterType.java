package edu.ualberta.med.biobank.reports.filters.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.report.ReportFilterValue;

public class BooleanFilterType implements FilterType {
    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {

        FilterTypeUtil.checkValues(values, 0, 0);

        switch (op) {
        case YES:
            criteria.add(Restrictions.eq(aliasedProperty, Boolean.TRUE));
            break;
        case NO:
            criteria.add(Restrictions.eq(aliasedProperty, Boolean.FALSE));
            break;
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.YES, FilterOperator.NO);
    }
}