package edu.ualberta.med.biobank.common.reports.filters.types;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.reports.ReportsUtil;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;

public class DateFilterType implements FilterType {
    private static final String DATE_TOKEN = "{date}";
    // TODO: put SQL date format somewhere it can be shared
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");

    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<String> values) {

        String sqlDateValue = values.isEmpty() ? null : values.get(0);

        switch (op) {
        case ON_OR_AFTER:
            criteria.add(Restrictions.ge(aliasedProperty, sqlDateValue));
            break;
        case ON_OR_BEFORE:
            criteria.add(Restrictions.le(aliasedProperty, sqlDateValue));
            break;
        case THIS_DAY:
        case THIS_WEEK:
        case THIS_MONTH:
        case THIS_YEAR:
            sqlDateValue = SQL_DATE_FORMAT.format(new Date());
        case SAME_DAY_AS:
        case SAME_WEEK_AS:
        case SAME_MONTH_AS:
        case SAME_YEAR_AS: {
            String sqlProperty = ReportsUtil.getSqlColumn(criteria,
                aliasedProperty);
            String modifier = null;

            switch (op) {
            case THIS_DAY:
            case SAME_DAY_AS:
                modifier = "DAY(" + DATE_TOKEN + ")";
                break;
            case THIS_WEEK:
            case SAME_WEEK_AS:
                modifier = "WEEK(" + DATE_TOKEN + ")";
                break;
            case THIS_MONTH:
            case SAME_MONTH_AS:
                modifier = "MONTH(" + DATE_TOKEN + ")";
                break;
            case THIS_YEAR:
            case SAME_YEAR_AS:
                modifier = "YEAR(" + DATE_TOKEN + ")";
                break;
            }

            if (modifier != null && sqlDateValue != null) {
                // include year to distinguish the same day, week, or month from
                // two different years
                if (op != FilterOperator.THIS_YEAR) {
                    modifier = "CONCAT(" + modifier + ", CONCAT('-', YEAR("
                        + DATE_TOKEN + ")))";
                }

                String sql = modifier.replace(DATE_TOKEN, sqlProperty) + " = "
                    + modifier.replace(DATE_TOKEN, "'" + sqlDateValue + "'");

                criteria.add(Restrictions.sqlRestriction(sql));
            }
        }
            break;
        }

    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.ON_OR_BEFORE,
            FilterOperator.ON_OR_AFTER, FilterOperator.THIS_DAY,
            FilterOperator.THIS_WEEK, FilterOperator.THIS_MONTH,
            FilterOperator.THIS_YEAR, FilterOperator.SAME_DAY_AS,
            FilterOperator.SAME_WEEK_AS, FilterOperator.SAME_MONTH_AS,
            FilterOperator.SAME_YEAR_AS);
    }
}