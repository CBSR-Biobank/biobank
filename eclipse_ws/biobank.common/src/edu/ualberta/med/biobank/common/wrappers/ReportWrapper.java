package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.wrappers.base.ReportBaseWrapper;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ReportWrapper extends ReportBaseWrapper {
    public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
    public static final String PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$
    public static final String PROPERTY_IS_COUNT = "isCount"; //$NON-NLS-1$
    public static final String PROPERTY_IS_PUBLIC = "isPublic"; //$NON-NLS-1$
    public static final String PROPERTY_USER_ID = "userId"; //$NON-NLS-1$
    public static final String REPORT_COLUMN_COLLECTION_CACHE_KEY =
        "reportColumnCollection"; //$NON-NLS-1$
    public static final String REPORT_FILTER_COLLECTION_CACHE_KEY =
        "reportFilterCollection"; //$NON-NLS-1$

    public ReportWrapper(WritableApplicationService appService, Report report) {
        super(appService, report);
    }

    public ReportWrapper(WritableApplicationService appService) {
        super(appService);
    }

    private static <E> Collection<E> notNull(Collection<E> collection) {
        if (collection == null) {
            return new ArrayList<E>();
        }
        return collection;
    }

    public ReportWrapper(ReportWrapper report) {
        super(report.getAppService());

        setName(report.wrappedObject.getName());
        setDescription(report.wrappedObject.getDescription());
        setIsCount(report.wrappedObject.getIsCount());
        setIsPublic(report.wrappedObject.getIsPublic());

        wrappedObject.setId(null);
        wrappedObject.setEntity(report.wrappedObject.getEntity());
        wrappedObject.setUserId(report.wrappedObject.getUserId());

        Collection<ReportColumn> reportColumns = new ArrayList<ReportColumn>();
        for (ReportColumn column : notNull(report.wrappedObject
            .getReportColumns())) {
            ReportColumn columnCopy = new ReportColumn();
            columnCopy.setEntityColumn(column.getEntityColumn());
            columnCopy.setPosition(column.getPosition());
            columnCopy.setPropertyModifier(column.getPropertyModifier());

            reportColumns.add(columnCopy);
        }
        setReportColumnCollection(reportColumns);

        Collection<ReportFilter> reportFilters = new ArrayList<ReportFilter>();
        for (ReportFilter filter : notNull(report.wrappedObject
            .getReportFilters())) {
            ReportFilter filterCopy = new ReportFilter();
            filterCopy.setEntityFilter(filter.getEntityFilter());
            filterCopy.setOperator(filter.getOperator());
            filterCopy.setPosition(filter.getPosition());

            Set<ReportFilterValue> values = new HashSet<ReportFilterValue>();
            for (ReportFilterValue value : notNull(filter
                .getReportFilterValues())) {
                ReportFilterValue valueCopy = new ReportFilterValue();
                valueCopy.setPosition(value.getPosition());
                valueCopy.setValue(value.getValue());
                valueCopy.setSecondValue(value.getSecondValue());

                values.add(valueCopy);
            }
            filterCopy.setReportFilterValues(values);

            reportFilters.add(filterCopy);
        }
        setReportFilterCollection(reportFilters);
    }

    public List<ReportColumn> getReportColumnCollection() {
        @SuppressWarnings("unchecked")
        List<ReportColumn> columns = (List<ReportColumn>) cache
            .get(REPORT_COLUMN_COLLECTION_CACHE_KEY);

        if (columns == null) {
            columns = new ArrayList<ReportColumn>();

            Collection<ReportColumn> rcc = wrappedObject
                .getReportColumns();
            if (rcc != null) {
                columns.addAll(rcc);
            }

            Collections.sort(columns, new Comparator<ReportColumn>() {
                @Override
                public int compare(ReportColumn lhs, ReportColumn rhs) {
                    return lhs.getPosition() - rhs.getPosition();
                }
            });

            cache.put(REPORT_COLUMN_COLLECTION_CACHE_KEY, columns);
        }

        return columns;
    }

    public void setReportColumnCollection(Collection<ReportColumn> reportColumns) {
        Collection<ReportColumn> oldReportColumns = wrappedObject
            .getReportColumns();

        Set<ReportColumn> newReportColumns = new HashSet<ReportColumn>();
        newReportColumns.addAll(reportColumns);

        wrappedObject.setReportColumns(newReportColumns);
        cache.remove(REPORT_COLUMN_COLLECTION_CACHE_KEY);
        propertyChangeSupport
            .firePropertyChange(REPORT_COLUMN_COLLECTION_CACHE_KEY,
                oldReportColumns, reportColumns);
    }

    public List<ReportFilter> getReportFilterCollection() {
        @SuppressWarnings("unchecked")
        List<ReportFilter> filters = (List<ReportFilter>) cache
            .get(REPORT_FILTER_COLLECTION_CACHE_KEY);

        if (filters == null) {
            filters = new ArrayList<ReportFilter>();

            Collection<ReportFilter> rfc = wrappedObject
                .getReportFilters();
            if (rfc != null) {
                filters.addAll(rfc);
            }

            Collections.sort(filters, new Comparator<ReportFilter>() {
                @Override
                public int compare(ReportFilter lhs, ReportFilter rhs) {
                    return lhs.getPosition() - rhs.getPosition();
                }
            });

            cache.put(REPORT_FILTER_COLLECTION_CACHE_KEY, filters);
        }

        return filters;
    }

    public void setReportFilterCollection(Collection<ReportFilter> reportFilters) {
        Collection<ReportFilter> oldReportFilters = wrappedObject
            .getReportFilters();

        Set<ReportFilter> newReportFilters = new HashSet<ReportFilter>();
        newReportFilters.addAll(reportFilters);

        wrappedObject.setReportFilters(newReportFilters);
        cache.remove(REPORT_FILTER_COLLECTION_CACHE_KEY);
        propertyChangeSupport
            .firePropertyChange(REPORT_FILTER_COLLECTION_CACHE_KEY,
                oldReportFilters, reportFilters);
    }

    public static Collection<String> getFilterValueStrings(
        ReportFilter reportFilter) {
        Collection<String> strings = new ArrayList<String>();
        Collection<ReportFilterValue> rfvCollection = reportFilter
            .getReportFilterValues();

        if (rfvCollection != null) {
            for (ReportFilterValue rfv : rfvCollection) {
                strings.add(rfv.getValue());
            }
        }

        return strings;
    }

    public Collection<EntityColumn> getEntityColumnCollection() {
        EntityWrapper entity = getEntity();
        if (entity != null) {
            return entity.getEntityColumnCollection();
        }
        return Arrays.asList();
    }

    public Collection<EntityFilter> getEntityFilterCollection() {
        EntityWrapper entity = getEntity();
        if (entity != null) {
            return entity.getEntityFilterCollection();
        }
        return Arrays.asList();
    }

    @Override
    public Boolean getIsCount() {
        Boolean isCount = super.getIsCount();
        return isCount == null || !isCount ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public int compareTo(ModelWrapper<Report> o) {
        if (o instanceof ReportWrapper) {
            return getName().compareTo(((ReportWrapper) o).getName());
        }
        return 0;
    }
}
