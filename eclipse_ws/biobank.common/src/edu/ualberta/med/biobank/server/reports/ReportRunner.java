package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.type.Type;

import edu.ualberta.med.biobank.common.reports.ReportsUtil;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.common.reports.filters.FilterTypes;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.PropertyModifier;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.server.applicationservice.ReportData;

public class ReportRunner {
    private static final String PROPERTY_DELIMITER = ".";
    private static final String ALIAS_DELIMITER = "-";
    private static final String PROPERTY_VALUE_TOKEN = "{value}";
    private static final String MODIFIED_PROPERTY_ALIAS = "_modifiedPropertyAlias";
    private static final Comparator<ReportColumn> COMPARE_REPORT_COLUMN_POSITION = new Comparator<ReportColumn>() {
        @Override
        public int compare(ReportColumn lhs, ReportColumn rhs) {
            return lhs.getPosition() - rhs.getPosition();
        }
    };

    private final Session session;
    private final Report report;
    private final Criteria criteria;

    public ReportRunner(Session session, ReportData data) {
        this.session = session;
        this.report = data.getReport();

        criteria = createCriteria();

        criteria.setMaxResults(data.getMaxResults());
        criteria.setFirstResult(data.getFirstRow());

        if (data.getTimeout() > 0) {
            criteria.setTimeout(data.getTimeout());
        }
    }

    public void setMaxResults(int maxResults) {
        criteria.setMaxResults(maxResults);
    }

    public void setTimeout(int timeoutInSeconds) {
        criteria.setTimeout(timeoutInSeconds);
    }

    public List<?> run() {
        if (criteria == null) {
            return Arrays.asList();
        }

        return criteria.list();
    }

    private Collection<ReportColumn> getOrderedReportColumns() {
        List<ReportColumn> orderedCols = new ArrayList<ReportColumn>();

        Collection<ReportColumn> reportCols = report
            .getReportColumnCollection();
        if (reportCols != null) {
            orderedCols.addAll(reportCols);
        }

        Collections.sort(orderedCols, COMPARE_REPORT_COLUMN_POSITION);

        return orderedCols;
    }

    private Criteria createCriteria() {
        if (!report.getIsCount()
            && report.getReportColumnCollection().isEmpty()) {
            return null;
        }

        Criteria criteria = session.createCriteria(report.getEntity()
            .getClassName());

        createAssociations(criteria);

        ProjectionList pList = Projections.projectionList();

        int colNum = 1;
        for (ReportColumn reportColumn : getOrderedReportColumns()) {
            String path = reportColumn.getEntityColumn().getEntityProperty()
                .getProperty();
            String aliasedProperty = getAliasedProperty(path);

            Projection projection = null;
            PropertyModifier propertyModifier = reportColumn
                .getPropertyModifier();
            if (propertyModifier != null) {
                String sqlColumn = ReportsUtil.getSqlColumn(criteria,
                    aliasedProperty);
                String modifier = propertyModifier.getPropertyModifier();
                String modifiedProperty = modifier.replace(
                    PROPERTY_VALUE_TOKEN, sqlColumn);
                String sqlAlias = MODIFIED_PROPERTY_ALIAS + colNum;

                if (report.getIsCount()) {
                    projection = Projections.sqlGroupProjection(
                        modifiedProperty + " as " + sqlAlias, sqlAlias,
                        new String[] { sqlAlias },
                        new Type[] { Hibernate.STRING });
                } else {
                    projection = Projections.sqlProjection(modifiedProperty
                        + " as " + sqlAlias, new String[] { sqlAlias },
                        new Type[] { Hibernate.STRING });
                }
            } else {
                if (report.getIsCount()) {
                    projection = Projections.groupProperty(aliasedProperty);
                } else {
                    projection = Projections.property(aliasedProperty);
                }
            }

            pList.add(projection);
            colNum++;
        }

        if (report.getIsCount()) {
            pList.add(Projections.countDistinct("id"));
        }

        criteria.setProjection(pList);

        Collection<ReportFilter> rfCollection = report
            .getReportFilterCollection();
        if (rfCollection != null) {
            for (ReportFilter reportFilter : rfCollection) {
                EntityFilter filter = reportFilter.getEntityFilter();
                FilterType filterType = FilterTypes.getFilterType(filter
                    .getFilterType());
                String propertyPath = filter.getEntityProperty().getProperty();
                String aliasedProperty = getAliasedProperty(propertyPath);

                Collection<ReportFilterValue> rfvCollection = reportFilter
                    .getReportFilterValueCollection();

                filterType
                    .addCriteria(criteria, aliasedProperty, FilterOperator
                        .getFilterOperator(reportFilter.getOperator()),
                        new ArrayList<ReportFilterValue>(rfvCollection));
            }
        }

        return criteria;
    }

    private void createAssociations(Criteria criteria) {
        Set<String> createdPoperties = new HashSet<String>();

        Collection<ReportColumn> cols = report.getReportColumnCollection();
        if (cols != null) {
            for (ReportColumn col : cols) {
                String property = col.getEntityColumn().getEntityProperty()
                    .getProperty();
                createAssociations(criteria, property, createdPoperties);
            }
        }

        Collection<ReportFilter> filters = report.getReportFilterCollection();
        if (filters != null) {
            for (ReportFilter filter : filters) {
                String property = filter.getEntityFilter().getEntityProperty()
                    .getProperty();
                createAssociations(criteria, property, createdPoperties);
            }
        }
    }

    private static void createAssociations(Criteria criteria, String property,
        Set<String> createdProperties) {
        String parentProperty = getParentProperty(property);
        while (parentProperty != null) {
            if (!createdProperties.contains(parentProperty)) {
                criteria.createCriteria(parentProperty,
                    getPropertyAlias(parentProperty));
                createdProperties.add(parentProperty);
            }
            parentProperty = getParentProperty(parentProperty);
        }
    }

    private static String getPropertyAlias(String property) {
        return property == null ? null : property.replace(PROPERTY_DELIMITER,
            ALIAS_DELIMITER);
    }

    private static String getParentProperty(String property) {
        String parentPath = null;
        int lastDelimiter = property.lastIndexOf(PROPERTY_DELIMITER);
        if (lastDelimiter != -1) {
            parentPath = property.substring(0, lastDelimiter);
        }
        return parentPath;
    }

    private static String getAliasedProperty(String property) {
        int lastDelimiter = property.lastIndexOf(PROPERTY_DELIMITER);
        if (lastDelimiter != -1) {
            String parentProperty = property.substring(0, lastDelimiter);
            String lastProperty = property.substring(lastDelimiter + 1);
            return getPropertyAlias(parentProperty) + PROPERTY_DELIMITER
                + lastProperty;
        }
        return property;
    }
}