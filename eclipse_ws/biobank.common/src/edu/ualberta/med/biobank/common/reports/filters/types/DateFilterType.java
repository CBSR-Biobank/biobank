package edu.ualberta.med.biobank.common.reports.filters.types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.reports.ReportsUtil;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class DateFilterType implements FilterType {
    private static final String DATE_TOKEN = "{date}";
    // TODO: put SQL date format somewhere it can be shared
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");

    private static Date getDateFromString(String string) {
        try {
            return SQL_DATE_FORMAT.parse(string);
        } catch (ParseException e) {
            throw new IllegalArgumentException("cannot parse date string '"
                + string + "'");
        }
    }

    private static Criterion between(String property, ReportFilterValue value) {
        Date minDate = getDateFromString(value.getValue());
        Date maxDate = getDateFromString(value.getSecondValue());

        return Restrictions.between(property, minDate, maxDate);
    }

    private static Date getDate(ReportFilterValue value) {
        return getDateFromString(value.getValue());
    }

    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {

        String sqlDateValue = null;
        Date date;

        switch (op) {
        case BETWEEN:
            FilterTypeUtil.checkValues(values, 1, 1);
            for (ReportFilterValue value : values) {
                criteria.add(between(aliasedProperty, value));
                break;
            }
            break;
        case BETWEEN_ANY: {
            FilterTypeUtil.checkValues(values, 1, FilterTypeUtil.NOT_BOUND);
            Disjunction or = Restrictions.disjunction();
            for (ReportFilterValue value : values) {
                or.add(between(aliasedProperty, value));
                break;
            }
            criteria.add(or);
        }
            break;
        case NOT_BETWEEN:
            FilterTypeUtil.checkValues(values, 1, 1);
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.not(between(aliasedProperty, value)));
                break;
            }
            break;
        case NOT_BETWEEN_ANY:
            FilterTypeUtil.checkValues(values, 1, 1);
            for (ReportFilterValue value : values) {
                criteria.add(Restrictions.not(between(aliasedProperty, value)));
            }
            break;
        case ON_OR_AFTER:
            FilterTypeUtil.checkValues(values, 1, 1);
            date = getDate(values.get(0));
            criteria.add(Restrictions.ge(aliasedProperty, date));
            break;
        case ON_OR_BEFORE:
            FilterTypeUtil.checkValues(values, 1, 1);
            date = getDate(values.get(0));
            criteria.add(Restrictions.le(aliasedProperty, date));
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
            if (sqlDateValue == null) {
                FilterTypeUtil.checkValues(values, 1, 1);
                sqlDateValue = values.get(0).getValue();
            }

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
            FilterOperator.SAME_YEAR_AS, FilterOperator.BETWEEN,
            FilterOperator.BETWEEN_ANY, FilterOperator.NOT_BETWEEN,
            FilterOperator.NOT_BETWEEN_ANY);
    }
}