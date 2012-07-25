package edu.ualberta.med.biobank.reports.filters.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.reports.filters.FilterType;
import edu.ualberta.med.biobank.reports.filters.SelectableFilterType;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ActivityStatusFilterType
    implements FilterType, SelectableFilterType {
    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {
        if (values != null && values.size() > 0) {
            Collection<ActivityStatus> statuses =
                new ArrayList<ActivityStatus>();
            for (ReportFilterValue value : values) {
                int id = Integer.parseInt(value.getValue());
                statuses.add(ActivityStatus.fromId(id));
            }

            switch (op) {
            case EQUALS:
                FilterTypeUtil.checkValues(values, 0, 1);
                criteria.add(Restrictions.eq(aliasedProperty, statuses
                    .iterator().next()));
                break;
            case DOES_NOT_EQUAL:
                FilterTypeUtil.checkValues(values, 0, 1);
                criteria.add(Restrictions.ne(aliasedProperty, statuses
                    .iterator().next()));
                break;
            case IS_IN:
                criteria.add(Restrictions.in(aliasedProperty,
                    statuses.toArray()));
                break;
            case IS_NOT_IN:
                criteria.add(Restrictions.not(Restrictions.in(aliasedProperty,
                    statuses.toArray())));
                break;
            }
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(
            FilterOperator.EQUALS,
            FilterOperator.DOES_NOT_EQUAL,
            FilterOperator.IS_IN,
            FilterOperator.IS_NOT_IN);
    }

    @Override
    public Map<String, String> getOptions(WritableApplicationService appService) {
        Map<String, String> options = new LinkedHashMap<String, String>();

        for (ActivityStatus status : ActivityStatus.valuesList()) {
            options.put(String.valueOf(status.getId()), status.getName());
        }

        return options;
    }
}