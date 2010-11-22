package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ReportWrapper extends ModelWrapper<Report> {
    private static final String PROP_KEY_NAME = "name";
    private static final String PROP_KEY_DESCRIPTION = "description";
    private static final String PROP_KEY_IS_COUNT = "isCount";
    private static final String PROP_KEY_USER_ID = "userId";
    private static final String PROP_KEY_REPORT_COLUMN_COLLECTION = "reportColumnCollection";
    private static final String PROP_KEY_REPORT_FILTER_COLLECTION = "reportFilterCollection";

    private EntityWrapper entity;

    public ReportWrapper(WritableApplicationService appService,
        Report wrappedObject) {
        super(appService, wrappedObject);
    }

    public ReportWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange(PROP_KEY_NAME, oldName, name);
    }

    public String getDescription() {
        return wrappedObject.getDescription();
    }

    public void setDescription(String description) {
        String oldDescription = getDescription();
        wrappedObject.setDescription(description);
        propertyChangeSupport.firePropertyChange(PROP_KEY_DESCRIPTION,
            oldDescription, description);
    }

    public Boolean getIsCount() {
        return wrappedObject.getIsCount();
    }

    public void setIsCount(Boolean isCount) {
        Boolean oldIsCount = getIsCount();
        wrappedObject.setIsCount(isCount);
        propertyChangeSupport.firePropertyChange(PROP_KEY_IS_COUNT, oldIsCount,
            isCount);
    }

    public Integer getUserId() {
        return wrappedObject.getUserId();
    }

    public void setUserId(Integer userId) {
        Integer oldUserId = getUserId();
        wrappedObject.setUserId(userId);
        propertyChangeSupport.firePropertyChange(PROP_KEY_USER_ID, oldUserId,
            userId);
    }

    @SuppressWarnings("unchecked")
    public List<ReportColumn> getReportColumnCollection() {
        List<ReportColumn> columns = (List<ReportColumn>) propertiesMap
            .get(PROP_KEY_REPORT_COLUMN_COLLECTION);

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

            propertiesMap.put(PROP_KEY_REPORT_COLUMN_COLLECTION, columns);
        }

        return columns;
    }

    @SuppressWarnings("unchecked")
    public List<ReportFilter> getReportFilterCollection() {
        List<ReportFilter> filters = (List<ReportFilter>) propertiesMap
            .get(PROP_KEY_REPORT_FILTER_COLLECTION);

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

            propertiesMap.put(PROP_KEY_REPORT_FILTER_COLLECTION, filters);
        }

        return filters;
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
    protected String[] getPropertyChangeNames() {
        return new String[] { PROP_KEY_NAME, PROP_KEY_DESCRIPTION,
            PROP_KEY_IS_COUNT, PROP_KEY_USER_ID,
            PROP_KEY_REPORT_COLUMN_COLLECTION,
            PROP_KEY_REPORT_FILTER_COLLECTION };
    }
}
