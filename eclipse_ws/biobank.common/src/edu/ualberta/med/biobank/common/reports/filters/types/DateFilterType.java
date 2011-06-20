package edu.ualberta.med.biobank.common.reports.filters.types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.reports.ReportsUtil;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class DateFilterType implements FilterType {
    private static final String DATE_TOKEN = "{date}"; //$NON-NLS-1$
    // TODO: put SQL date format somewhere it can be shared
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {

        switch (op) {
        case IS_NOT_SET: {
            FilterTypeUtil.checkValues(values, 0, 0);
            criteria.add(ReportsUtil.isNotSet(aliasedProperty));
        }
            break;
        case BETWEEN:
            FilterTypeUtil.checkValues(values, 0, 1);
            for (ReportFilterValue value : values) {
                criteria.add(between(aliasedProperty, value));
                break;
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
            FilterTypeUtil.checkValues(values, 1, 1);
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
        case ON_OR_AFTER: {
            FilterTypeUtil.checkValues(values, 0, 1);
            for (ReportFilterValue value : values) {
                Date date = getDate(value);
                criteria.add(Restrictions.ge(aliasedProperty, date));
            }
        }
            break;
        case ON_OR_BEFORE: {
            FilterTypeUtil.checkValues(values, 0, 1);
            for (ReportFilterValue value : values) {
                Date date = getDate(value);
                criteria.add(Restrictions.le(aliasedProperty, date));
            }
        }
            break;
        case THIS_DAY:
        case THIS_WEEK:
        case THIS_MONTH:
        case THIS_YEAR: {
            FilterTypeUtil.checkValues(values, 0, 0);

            String dateString = SQL_DATE_FORMAT.format(new Date());
            String sqlColumn = ReportsUtil.getSqlColumn(criteria,
                aliasedProperty);

            criteria.add(getDateFieldCriterion(op, sqlColumn, dateString));
        }
            break;
        case SAME_DAY_AS_ANY:
        case SAME_WEEK_AS_ANY:
        case SAME_MONTH_AS_ANY:
        case SAME_YEAR_AS_ANY: {
            Disjunction or = Restrictions.disjunction();
            for (ReportFilterValue value : values) {
                String dateString = value.getValue();
                String sqlColumn = ReportsUtil.getSqlColumn(criteria,
                    aliasedProperty);

                or.add(getDateFieldCriterion(op, sqlColumn, dateString));
            }
            criteria.add(or);
        }
            break;
        }

    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.ON_OR_BEFORE,
            FilterOperator.ON_OR_AFTER, FilterOperator.IS_NOT_SET,
            FilterOperator.THIS_DAY, FilterOperator.THIS_WEEK,
            FilterOperator.THIS_MONTH, FilterOperator.THIS_YEAR,
            FilterOperator.SAME_DAY_AS_ANY, FilterOperator.SAME_WEEK_AS_ANY,
            FilterOperator.SAME_MONTH_AS_ANY, FilterOperator.SAME_YEAR_AS_ANY,
            FilterOperator.BETWEEN, FilterOperator.BETWEEN_ANY,
            FilterOperator.NOT_BETWEEN, FilterOperator.NOT_BETWEEN_ANY);
    }

    private static Date getDateFromString(String string) {
        try {
            return SQL_DATE_FORMAT.parse(string);
        } catch (ParseException e) {
            throw new IllegalArgumentException("cannot parse date string '" //$NON-NLS-1$
                + string + "'"); //$NON-NLS-1$
        }
    }

    private static Criterion between(String property, ReportFilterValue value) {
        Date minDate = getDate(value);
        Date maxDate = getSecondDate(value);

        return Restrictions.between(property, minDate, maxDate);
    }

    private static Date getDate(ReportFilterValue value) {
        return getDateFromString(value.getValue());
    }

    private static Date getSecondDate(ReportFilterValue value) {
        return getDateFromString(value.getSecondValue());
    }

    private static Criterion getDateFieldCriterion(FilterOperator op,
        String sqlColumn, String dateString) {

        String fieldSql = getDateFieldSql(op, DATE_TOKEN);

        String sql = fieldSql.replace(DATE_TOKEN, sqlColumn) + " = " //$NON-NLS-1$
            + fieldSql.replace(DATE_TOKEN, "'" + dateString + "'"); //$NON-NLS-1$ //$NON-NLS-2$

        return Restrictions.sqlRestriction(sql);
    }

    private static String getDateFieldSql(FilterOperator op, final String token) {
        String modifier = null;

        switch (op) {
        case THIS_DAY:
        case SAME_DAY_AS_ANY:
            modifier = "DAY(" + token + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            break;
        case THIS_WEEK:
        case SAME_WEEK_AS_ANY:
            modifier = "WEEK(" + token + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            break;
        case THIS_MONTH:
        case SAME_MONTH_AS_ANY:
            modifier = "MONTH(" + token + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            break;
        case THIS_YEAR:
        case SAME_YEAR_AS_ANY:
            modifier = "YEAR(" + token + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            break;
        }

        if (modifier != null) {
            // include year to distinguish the same day, week, or month from
            // two different years
            if (op != FilterOperator.THIS_YEAR) {
                modifier = "CONCAT(" + modifier + ", CONCAT('-', YEAR(" + token //$NON-NLS-1$ //$NON-NLS-2$
                    + ")))"; //$NON-NLS-1$
            }
        }

        return modifier;
    }
}