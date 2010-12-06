package edu.ualberta.med.biobank.common.reports.filters.types;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.reports.ReportsUtil;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class TopContainerFilterType implements FilterType {
    // TODO: is it any better to write this in HQL and convert it to SQL?
    private static final String SQL = "{0} {1} (select cp1.container_id from container_path cp1, container_path cp2 where cp1.path like concat(cp2.path, ''/%'') and cp2.container_id in ({2}))";

    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {
        if (values != null && values.size() > 0) {
            Collection<String> stringValues = new ArrayList<String>();
            for (ReportFilterValue value : values) {
                stringValues.add(value.getValue());
            }

            String idList = StringUtils.join(stringValues.toArray(), ",");
            String sqlProperty = ReportsUtil.getSqlColumn(criteria,
                aliasedProperty);
            String setOp = op == FilterOperator.IN ? "in" : "not in";
            String sql = MessageFormat.format(SQL, sqlProperty, setOp, idList);
            criteria.add(Restrictions.sqlRestriction(sql));
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.IN, FilterOperator.NOT_IN);
    }

}