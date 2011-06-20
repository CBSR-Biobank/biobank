package edu.ualberta.med.biobank.server.reports;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
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
    private static final String ID_COLUMN_NAME = "id"; //$NON-NLS-1$
    private static final String PROPERTY_DELIMITER = "."; //$NON-NLS-1$
    private static final String ALIAS_DELIMITER = "__"; //$NON-NLS-1$
    private static final String PROPERTY_VALUE_TOKEN = "{value}"; //$NON-NLS-1$
    private static final String MODIFIED_PROPERTY_ALIAS = "_modifiedPropertyAlias"; //$NON-NLS-1$
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

        if (criteria != null) {
            criteria.setMaxResults(data.getMaxResults());
            criteria.setFirstResult(data.getFirstRow());

            if (data.getTimeout() > 0) {
                criteria.setTimeout(data.getTimeout());
            }
        }
    }

    public List<?> run() {
        if (criteria == null) {
            return Arrays.asList();
        }

        return criteria.list();
    }

    private Collection<ReportColumn> getOrderedReportColumns() {
        List<ReportColumn> orderedCols = new ArrayList<ReportColumn>();

        loadProperty(report, "reportColumnCollection"); //$NON-NLS-1$
        Collection<ReportColumn> reportCols = report
            .getReportColumnCollection();
        if (reportCols != null) {
            orderedCols.addAll(reportCols);
        }

        Collections.sort(orderedCols, COMPARE_REPORT_COLUMN_POSITION);

        return orderedCols;
    }

    private boolean isCount() {
        Boolean isCount = report.getIsCount();
        return isCount == null ? false : isCount;
    }

    private Criteria createCriteria() {
        loadProperty(report, "reportColumnCollection"); //$NON-NLS-1$
        if (!isCount() && report.getReportColumnCollection().isEmpty()) {
            return null;
        }

        loadProperty(report, "entity"); //$NON-NLS-1$
        Criteria criteria = session.createCriteria(report.getEntity()
            .getClassName());

        createAssociations(criteria);

        ProjectionList pList = Projections.projectionList();

        if (!isCount()) {
            pList.add(Projections.property(ID_COLUMN_NAME));
        } else {
            // need to provide an alias for the column to be included in the
            // results
            pList.add(Projections.sqlProjection("NULL as null_value_", //$NON-NLS-1$
                new String[] { "null_value_" }, //$NON-NLS-1$
                new Type[] { Hibernate.INTEGER }));
        }

        int colNum = 1;
        for (ReportColumn reportColumn : getOrderedReportColumns()) {
            loadProperty(reportColumn, "entityColumn"); //$NON-NLS-1$
            loadProperty(reportColumn.getEntityColumn(), "entityProperty"); //$NON-NLS-1$
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

                if (isCount()) {
                    projection = Projections.sqlGroupProjection(
                        modifiedProperty + " as " + sqlAlias, sqlAlias, //$NON-NLS-1$
                        new String[] { sqlAlias },
                        new Type[] { Hibernate.STRING });
                } else {
                    projection = Projections.sqlProjection(modifiedProperty
                        + " as " + sqlAlias, new String[] { sqlAlias }, //$NON-NLS-1$
                        new Type[] { Hibernate.STRING });
                }
            } else {
                if (isCount()) {
                    projection = Projections.groupProperty(aliasedProperty);
                } else {
                    projection = Projections.property(aliasedProperty);
                }
            }

            pList.add(projection);
            colNum++;
        }

        if (isCount()) {
            pList.add(Projections.countDistinct(ID_COLUMN_NAME));
        }

        criteria.setProjection(pList);

        loadProperty(report, "reportFilterCollection"); //$NON-NLS-1$
        Collection<ReportFilter> rfCollection = report
            .getReportFilterCollection();
        if (rfCollection != null) {
            for (ReportFilter reportFilter : rfCollection) {
                loadProperty(reportFilter, "entityFilter"); //$NON-NLS-1$
                loadProperty(reportFilter.getEntityFilter(), "entityProperty"); //$NON-NLS-1$

                EntityFilter filter = reportFilter.getEntityFilter();
                FilterType filterType = FilterTypes.getFilterType(filter
                    .getFilterType());
                String propertyPath = filter.getEntityProperty().getProperty();
                String aliasedProperty = getAliasedProperty(propertyPath);

                Collection<ReportFilterValue> rfvCollection = reportFilter
                    .getReportFilterValueCollection();

                FilterOperator op = null;

                if (reportFilter.getOperator() != null) {
                    op = FilterOperator.getFilterOperator(reportFilter
                        .getOperator());
                }

                filterType.addCriteria(criteria, aliasedProperty, op,
                    new ArrayList<ReportFilterValue>(rfvCollection));
            }
        }

        return criteria;
    }

    /**
     * Read the property of the given <code>Object</code>. If the property
     * exists and is an uninitialized Hibernate proxy object, then replace it
     * with a copy from the database.
     * 
     * @param object
     * @param property
     */
    private void loadProperty(Object object, String property) {
        try {
            Class<?> objectKlazz = object.getClass();

            String methodSuffix = Character.toUpperCase(property.charAt(0))
                + property.substring(1);

            Method getProperty = objectKlazz.getMethod("get" + methodSuffix); //$NON-NLS-1$

            Class<?> propertyKlazz = getProperty.getReturnType();

            Method setProperty = objectKlazz.getMethod("set" + methodSuffix, //$NON-NLS-1$
                propertyKlazz);

            Object propertyValue = getProperty.invoke(object);

            if (Hibernate.isInitialized(propertyValue)) {
                return;
            }

            // TODO: treat Collection-s differently
            Class<?> proxyKlazz = propertyValue.getClass();
            Method getId = proxyKlazz.getMethod("getId"); //$NON-NLS-1$
            Serializable id = (Serializable) getId.invoke(propertyValue);

            Object databaseObject = null;
            if (id != null) {
                databaseObject = session.load(propertyKlazz, id);
            }

            setProperty.invoke(object, databaseObject);
        } catch (Exception e) {
            // TODO: log this?
        }
    }

    private void createAssociations(Criteria criteria) {
        Set<String> createdPoperties = new HashSet<String>();

        loadProperty(report, "reportColumnCollection"); //$NON-NLS-1$
        Collection<ReportColumn> cols = report.getReportColumnCollection();
        if (cols != null) {
            for (ReportColumn reportColumn : cols) {
                loadProperty(reportColumn, "entityColumn"); //$NON-NLS-1$
                loadProperty(reportColumn.getEntityColumn(), "entityProperty"); //$NON-NLS-1$

                String property = reportColumn.getEntityColumn()
                    .getEntityProperty().getProperty();
                createAssociations(criteria, property, createdPoperties);
            }
        }

        loadProperty(report, "reportFilterCollection"); //$NON-NLS-1$
        Collection<ReportFilter> filters = report.getReportFilterCollection();
        if (filters != null) {
            for (ReportFilter filter : filters) {
                loadProperty(filter, "entityFilter"); //$NON-NLS-1$
                loadProperty(filter.getEntityFilter(), "entityProperty"); //$NON-NLS-1$

                String property = filter.getEntityFilter().getEntityProperty()
                    .getProperty();
                createAssociations(criteria, property, createdPoperties);
            }
        }
    }

    public static void createAssociations(Criteria criteria, String property) {
        Set<String> createdProperties = getCreatedAssociations(criteria);

        createAssociations(criteria, property, createdProperties);
    }

    private static Set<String> getCreatedAssociations(Criteria criteria) {
        Set<String> createdProperties = new HashSet<String>();

        @SuppressWarnings("rawtypes")
        Iterator subcriterias = ((CriteriaImpl) criteria).iterateSubcriteria();
        while (subcriterias.hasNext()) {
            Criteria subcriteria = (Criteria) subcriterias.next();
            String property = getProperty(subcriteria.getAlias());
            if (property != null) {
                createdProperties.add(property);
            }
        }

        return createdProperties;
    }

    private static void createAssociations(Criteria criteria, String property,
        Set<String> createdProperties) {
        String parentProperty = getParentProperty(property);
        while (parentProperty != null) {
            if (!createdProperties.contains(parentProperty)) {
                int joinType = Criteria.INNER_JOIN;

                // TODO: do not hardcode "specimenPosition.", read a list or
                // config from somewhere. This is necessary because some
                // aliquots legitimately do not have a position in a container
                if (parentProperty.equals("specimenPosition") //$NON-NLS-1$
                    || parentProperty.startsWith("specimenPosition.")) { //$NON-NLS-1$
                    joinType = Criteria.LEFT_JOIN;
                }

                criteria.createCriteria(parentProperty,
                    getPropertyAlias(parentProperty), joinType);
                createdProperties.add(parentProperty);
            }
            parentProperty = getParentProperty(parentProperty);
        }
    }

    private static String getPropertyAlias(String property) {
        // TODO: do we need to swap delimiters? Can the alias name just include
        // the period?
        return property == null ? null : property.replace(PROPERTY_DELIMITER,
            ALIAS_DELIMITER);
    }

    public static String getProperty(String parentProperty,
        String... childProperties) {
        StringBuilder sb = new StringBuilder();

        if (parentProperty != null) {
            sb.append(parentProperty);
            sb.append(PROPERTY_DELIMITER);
        }

        sb.append(StringUtils.join(childProperties, PROPERTY_DELIMITER));

        return sb.toString();
    }

    public static String getParentProperty(String property) {
        String parentPath = null;
        int lastDelimiter = property.lastIndexOf(PROPERTY_DELIMITER);
        if (lastDelimiter != -1) {
            parentPath = property.substring(0, lastDelimiter);
        }
        return parentPath;
    }

    public static String getProperty(String alias) {
        return alias == null ? null : alias.replace(ALIAS_DELIMITER,
            PROPERTY_DELIMITER);
    }

    public static String getAliasedProperty(String property) {
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