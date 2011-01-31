package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ReportPeer;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ReportWrapper extends ModelWrapper<Report> {
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_IS_COUNT = "isCount";
    public static final String PROPERTY_IS_PUBLIC = "isPublic";
    public static final String PROPERTY_USER_ID = "userId";
    public static final String PROPERTY_REPORT_COLUMN_COLLECTION = "reportColumnCollection";
    public static final String PROPERTY_REPORT_FILTER_COLLECTION = "reportFilterCollection";

    private EntityWrapper entity;

    public ReportWrapper(WritableApplicationService appService, Report report) {
        super(appService, report);
    }

    public ReportWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ReportWrapper(ReportWrapper report) {
        super(report.getAppService());

        setName(report.getName());
        setDescription(report.getDescription());
        setIsCount(report.getIsCount());
        setIsPublic(report.getIsPublic());

        wrappedObject.setEntity(report.getWrappedObject().getEntity());
        wrappedObject.setUserId(report.getWrappedObject().getUserId());

        Collection<ReportColumn> reportColumns = new ArrayList<ReportColumn>();
        for (ReportColumn column : report.getReportColumnCollection()) {
            ReportColumn columnCopy = new ReportColumn();
            columnCopy.setEntityColumn(column.getEntityColumn());
            columnCopy.setPosition(column.getPosition());
            columnCopy.setPropertyModifier(column.getPropertyModifier());

            reportColumns.add(columnCopy);
        }
        setReportColumnCollection(reportColumns);

        Collection<ReportFilter> reportFilters = new ArrayList<ReportFilter>();
        for (ReportFilter filter : report.getReportFilterCollection()) {
            ReportFilter filterCopy = new ReportFilter();
            filterCopy.setEntityFilter(filter.getEntityFilter());
            filterCopy.setOperator(filter.getOperator());
            filterCopy.setPosition(filter.getPosition());

            Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
            for (ReportFilterValue value : filter
                .getReportFilterValueCollection()) {
                ReportFilterValue valueCopy = new ReportFilterValue();
                valueCopy.setPosition(value.getPosition());
                valueCopy.setValue(value.getValue());
                valueCopy.setSecondValue(value.getSecondValue());
            }
            filterCopy.setReportFilterValueCollection(values);

            reportFilters.add(filterCopy);
        }
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange(PROPERTY_NAME, oldName, name);
    }

    public String getDescription() {
        return wrappedObject.getDescription();
    }

    public void setDescription(String description) {
        String oldDescription = getDescription();
        wrappedObject.setDescription(description);
        propertyChangeSupport.firePropertyChange(PROPERTY_DESCRIPTION,
            oldDescription, description);
    }

    public Boolean getIsCount() {
        Boolean bool = wrappedObject.getIsCount();
        return bool != null ? bool : false;
    }

    public void setIsCount(Boolean isCount) {
        Boolean oldIsCount = getIsCount();
        wrappedObject.setIsCount(isCount);
        propertyChangeSupport.firePropertyChange(PROPERTY_IS_COUNT, oldIsCount,
            isCount);
    }

    public Boolean getIsPublic() {
        Boolean bool = wrappedObject.getIsPublic();
        return bool != null ? bool : false;
    }

    public void setIsPublic(Boolean isPublic) {
        Boolean oldIsPublic = getIsPublic();
        wrappedObject.setIsPublic(isPublic);
        propertyChangeSupport.firePropertyChange(PROPERTY_IS_PUBLIC,
            oldIsPublic, isPublic);
    }

    public Integer getUserId() {
        return wrappedObject.getUserId();
    }

    public void setUserId(Integer userId) {
        Integer oldUserId = getUserId();
        wrappedObject.setUserId(userId);
        propertyChangeSupport.firePropertyChange(PROPERTY_USER_ID, oldUserId,
            userId);
    }

    public List<ReportColumn> getReportColumnCollection() {
        @SuppressWarnings("unchecked")
        List<ReportColumn> columns = (List<ReportColumn>) propertiesMap
            .get(PROPERTY_REPORT_COLUMN_COLLECTION);

        if (columns == null) {
            columns = new ArrayList<ReportColumn>();

            Collection<ReportColumn> rcc = wrappedObject
                .getReportColumnCollection();
            if (rcc != null) {
                columns.addAll(rcc);
            }

            Collections.sort(columns, new Comparator<ReportColumn>() {
                @Override
                public int compare(ReportColumn lhs, ReportColumn rhs) {
                    return lhs.getPosition() - rhs.getPosition();
                }
            });

            propertiesMap.put(PROPERTY_REPORT_COLUMN_COLLECTION, columns);
        }

        return columns;
    }

    public void setReportColumnCollection(Collection<ReportColumn> reportColumns) {
        Collection<ReportColumn> oldReportColumns = wrappedObject
            .getReportColumnCollection();

        Set<ReportColumn> newReportColumns = new HashSet<ReportColumn>();
        newReportColumns.addAll(reportColumns);

        wrappedObject.setReportColumnCollection(newReportColumns);
        propertiesMap.remove(PROPERTY_REPORT_COLUMN_COLLECTION);
        propertyChangeSupport.firePropertyChange(
            PROPERTY_REPORT_COLUMN_COLLECTION, oldReportColumns, reportColumns);
    }

    public List<ReportFilter> getReportFilterCollection() {
        @SuppressWarnings("unchecked")
        List<ReportFilter> filters = (List<ReportFilter>) propertiesMap
            .get(PROPERTY_REPORT_FILTER_COLLECTION);

        if (filters == null) {
            filters = new ArrayList<ReportFilter>();

            Collection<ReportFilter> rfc = wrappedObject
                .getReportFilterCollection();
            if (rfc != null) {
                filters.addAll(rfc);
            }

            Collections.sort(filters, new Comparator<ReportFilter>() {
                @Override
                public int compare(ReportFilter lhs, ReportFilter rhs) {
                    return lhs.getPosition() - rhs.getPosition();
                }
            });

            propertiesMap.put(PROPERTY_REPORT_FILTER_COLLECTION, filters);
        }

        return filters;
    }

    public void setReportFilterCollection(Collection<ReportFilter> reportFilters) {
        Collection<ReportFilter> oldReportFilters = wrappedObject
            .getReportFilterCollection();

        Set<ReportFilter> newReportFilters = new HashSet<ReportFilter>();
        newReportFilters.addAll(reportFilters);

        wrappedObject.setReportFilterCollection(newReportFilters);
        propertiesMap.remove(PROPERTY_REPORT_FILTER_COLLECTION);
        propertyChangeSupport.firePropertyChange(
            PROPERTY_REPORT_FILTER_COLLECTION, oldReportFilters, reportFilters);
    }

    public static Collection<String> getFilterValueStrings(
        ReportFilter reportFilter) {
        Collection<String> strings = new ArrayList<String>();
        Collection<ReportFilterValue> rfvCollection = reportFilter
            .getReportFilterValueCollection();

        if (rfvCollection != null) {
            for (ReportFilterValue rfv : rfvCollection) {
                strings.add(rfv.getValue());
            }
        }

        return strings;
    }

    public EntityWrapper getEntity() {
        if (entity == null) {
            entity = new EntityWrapper(appService, wrappedObject.getEntity());
        }
        return entity;
    }

    public Collection<EntityColumn> getEntityColumnCollection() {
        return getEntity().getEntityColumnCollection();
    }

    public Collection<EntityFilter> getEntityFilterCollection() {
        return getEntity().getEntityFilterCollection();
    }

    @Override
    public Class<Report> getWrappedClass() {
        return Report.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ReportPeer.PROP_NAMES;
    }

    @Override
    public int compareTo(ModelWrapper<Report> o) {
        if (o instanceof ReportWrapper) {
            return getName().compareTo(((ReportWrapper) o).getName());
        }
        return 0;
    }
}
