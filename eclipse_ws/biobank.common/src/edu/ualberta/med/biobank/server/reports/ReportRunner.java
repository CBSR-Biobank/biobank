package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.reports.ReportInput;
import edu.ualberta.med.biobank.common.action.reports.ReportInput.ReportColumnInput;
import edu.ualberta.med.biobank.common.action.reports.ReportInput.ReportFilterInput;
import edu.ualberta.med.biobank.common.action.reports.ReportInput.ReportFilterValueInput;
import edu.ualberta.med.biobank.common.reports.ReportsUtil;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.common.reports.filters.FilterTypes;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.PropertyModifier;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.server.applicationservice.ReportData;

@SuppressWarnings("nls")
public class ReportRunner {

    private static Logger log = LoggerFactory.getLogger(ReportRunner.class);

    private static final String ID_COLUMN_NAME = "id";

    private static final String PROPERTY_DELIMITER = ".";

    private static final String ALIAS_DELIMITER = "__";

    private static final String PROPERTY_VALUE_TOKEN = "{value}";

    private static final String MODIFIED_PROPERTY_ALIAS = "_modifiedPropertyAlias";

    private static final Comparator<ReportColumn> COMPARE_REPORT_COLUMN_POSITION =
        new Comparator<ReportColumn>() {
            @Override
            public int compare(ReportColumn lhs, ReportColumn rhs) {
                return lhs.getPosition() - rhs.getPosition();
            }
        };

    private final Session session;

    private final ReportInput reportInput;

    private final Criteria criteria;

    public ReportRunner(Session session, ReportData data) {
        this.session = session;
        this.reportInput = data.getReportInput();

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

        List<?> results = criteria.list();
        log.info("run: results size: {}", results.size());
        return results;
    }

    private Set<ReportColumn> getReportColumns() {
        Set<ReportColumn> columns = new HashSet<ReportColumn>();

        Set<ReportColumnInput> columnInputs = reportInput.getReportColumnInputs();
        if (columnInputs == null) {
            throw new IllegalStateException("column input is null");
        }

        for (ReportColumnInput columnInput : columnInputs) {
            ReportColumn column = new ReportColumn();
            column.setPosition(columnInput.getPosition());

            PropertyModifier propertyModifier = null;
            Integer propertyModifierId = columnInput.getPropertyModifierId();
            if (propertyModifierId != null) {
                propertyModifier = (PropertyModifier) session.load(
                    PropertyModifier.class, columnInput.getPropertyModifierId());
            }
            column.setPropertyModifier(propertyModifier);

            EntityColumn entityColumn = (EntityColumn) session.load(
                EntityColumn.class, columnInput.getEntityColumnId());
            column.setEntityColumn(entityColumn);

            log.trace("getReportColumns: {}", column);
            columns.add(column);
        }

        return columns;
    }

    private Set<ReportFilter> getReportFilters() {
        Set<ReportFilter> filters = new HashSet<ReportFilter>();

        Set<ReportFilterInput> filterInputs = reportInput.getReportFilterInputs();
        if (filterInputs == null) {
            throw new IllegalStateException("filter input is null");
        }

        for (ReportFilterInput filterInput : filterInputs) {
            ReportFilter filter = new ReportFilter();

            EntityFilter entityFilter = (EntityFilter) session.load(
                EntityFilter.class, filterInput.getEntityFilterId());

            filter.setPosition(filterInput.getPosition());
            filter.setOperator(filterInput.getOperator());
            filter.setEntityFilter(entityFilter);

            Set<ReportFilterValueInput> filterValueInputs = filterInput.getFilterValueInputs();
            Set<ReportFilterValue> filterValues =
                new HashSet<ReportFilterValue>(filterValueInputs.size());

            for (ReportFilterValueInput valueInput : filterValueInputs) {
                ReportFilterValue filterValue = new ReportFilterValue();
                filterValue.setPosition(valueInput.getPosition());
                filterValue.setValue(valueInput.getValue());
                filterValue.setSecondValue(valueInput.getSecondValue());
                filterValues.add(filterValue);
            }

            filter.setReportFilterValues(filterValues);
            log.trace("getReportFilters: {}", filter);
            filters.add(filter);
        }
        return filters;
    }

    private Criteria createCriteria() {
        if (!reportInput.isCount() && reportInput.getReportColumnInputs().isEmpty()) {
            return null;
        }

        Entity entity = (Entity) session.load(Entity.class, reportInput.getEntityId());
        Criteria criteria = session.createCriteria(entity.getClassName());

        Set<ReportColumn> reportColumns = getReportColumns();
        Set<ReportFilter> reportFilters = getReportFilters();
        createAssociations(criteria, reportColumns, reportFilters);

        ProjectionList pList = Projections.projectionList();

        if (!reportInput.isCount()) {
            pList.add(Projections.property(ID_COLUMN_NAME));
        } else {
            // need to provide an alias for the column to be included in the
            // results
            pList.add(Projections.sqlProjection(
                "NULL as null_value_",
                new String[] { "null_value_" },
                new Type[] { StandardBasicTypes.INTEGER }));
        }

        List<ReportColumn> orderedColumns = new ArrayList<ReportColumn>(reportColumns);
        Collections.sort(orderedColumns, COMPARE_REPORT_COLUMN_POSITION);
        int colNum = 1;
        for (ReportColumn reportColumn : orderedColumns) {
            String path = reportColumn.getEntityColumn().getEntityProperty().getProperty();
            String aliasedProperty = getAliasedProperty(path);

            Projection projection = null;
            PropertyModifier propertyModifier = reportColumn.getPropertyModifier();
            if (propertyModifier != null) {
                String sqlColumn = ReportsUtil.getSqlColumn(criteria, aliasedProperty);
                String modifier = propertyModifier.getPropertyModifier();
                String modifiedProperty = modifier.replace(PROPERTY_VALUE_TOKEN, sqlColumn);
                String sqlAlias = MODIFIED_PROPERTY_ALIAS + colNum;

                if (reportInput.isCount()) {
                    projection = Projections.sqlGroupProjection(
                        modifiedProperty + " as " + sqlAlias, sqlAlias,
                        new String[] { sqlAlias },
                        new Type[] { StandardBasicTypes.STRING });
                } else {
                    projection = Projections.sqlProjection(modifiedProperty
                        + " as " + sqlAlias, new String[] { sqlAlias },
                        new Type[] { StandardBasicTypes.STRING });
                }
            } else {
                if (reportInput.isCount()) {
                    projection = Projections.groupProperty(aliasedProperty);
                } else {
                    projection = Projections.property(aliasedProperty);
                }
            }

            pList.add(projection);
            colNum++;
        }

        if (reportInput.isCount()) {
            pList.add(Projections.countDistinct(ID_COLUMN_NAME));
        }

        criteria.setProjection(pList);

        Set<ReportFilterInput> filterInputs = reportInput.getReportFilterInputs();
        if (filterInputs == null) {
            throw new IllegalStateException("filter input is null");
        }

        for (ReportFilter reportFilter : reportFilters) {
            EntityFilter entityFilter = reportFilter.getEntityFilter();
            FilterType filterType = FilterTypes.getFilterType(entityFilter.getFilterType());
            String propertyPath = entityFilter.getEntityProperty().getProperty();
            String aliasedProperty = getAliasedProperty(propertyPath);

            FilterOperator op = FilterOperator.getFilterOperator(reportFilter.getOperator());

            filterType.addCriteria(criteria, aliasedProperty, op,
                new ArrayList<ReportFilterValue>(reportFilter.getReportFilterValues()));
        }

        return criteria;
    }

    private void createAssociations(
        Criteria criteria,
        Set<ReportColumn> reportColumns,
        Set<ReportFilter> reportFilters) {
        Set<String> createdPoperties = new HashSet<String>();

        for (ReportColumn reportColumn : reportColumns) {
            String property = reportColumn.getEntityColumn().getEntityProperty().getProperty();
            createAssociations(criteria, property, createdPoperties);
        }

        for (ReportFilter filter : reportFilters) {
            String property = filter.getEntityFilter().getEntityProperty().getProperty();
            createAssociations(criteria, property, createdPoperties);
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

    private static void createAssociations(
        Criteria criteria,
        String property,
        Set<String> createdProperties) {

        String parentProperty = getParentProperty(property);
        while (parentProperty != null) {
            if (!createdProperties.contains(parentProperty)) {
                // Always use a left join to support the "is not set" option of many filters.
                criteria.createCriteria(
                    parentProperty,
                    getPropertyAlias(parentProperty),
                    Criteria.LEFT_JOIN);
                createdProperties.add(parentProperty);
            }
            parentProperty = getParentProperty(parentProperty);
        }
    }

    private static String getPropertyAlias(String property) {
        // TODO: do we need to swap delimiters? Can the alias name just include the period?
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