package edu.ualberta.med.biobank.test.reports;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.reports.ReportInput;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.common.reports.filters.FilterTypes;
import edu.ualberta.med.biobank.common.wrappers.EntityWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.EntityProperty;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.PropertyType;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.TestDatabase;
import gov.nih.nci.system.applicationservice.ApplicationException;

@SuppressWarnings("all")
@Ignore
public class TestAdvancedReports extends TestDatabase {
    private static final SimpleDateFormat SQL_DATE_FORMAT =
        new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private final Map<String, Entity> entityMap = new HashMap<String, Entity>();

    @Before
    public void getEntities() {
        Collection<Entity> entities = EntityWrapper.getEntities(appService,
            EntityWrapper.ORDER_BY_NAME);

        for (Entity entity : entities) {
            entityMap.put(entity.getClassName(), entity);
        }
    }

    @Test
    public void testSpecimen() {
        Entity entity = entityMap.get(Specimen.class.getName());
        testEntity(entity);
    }

    @Test
    public void testContainer() {
        Entity entity = entityMap.get(Container.class.getName());
        testEntity(entity);
    }

    @Test
    public void testPatient() {
        Entity entity = entityMap.get(Patient.class.getName());
        testEntity(entity);
    }

    @Test
    public void testCollectionEvent() {
        Entity entity = entityMap.get(CollectionEvent.class.getName());
        testEntity(entity);
    }

    @Test
    public void testProcessingEvent() {
        Entity entity = entityMap.get(ProcessingEvent.class.getName());
        testEntity(entity);
    }

    private void testEntity(Entity entity) {
        Report report = new Report();
        report.setEntity(entity);
        report.setIsPublic(true);

        try {
            for (EntityProperty property : entity.getEntityProperties()) {
                testColumns(report, property);
                testFilters(report, property);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private void testColumns(Report report, EntityProperty property)
        throws ApplicationException {
        Collection<EntityColumn> entityColumns = property
            .getEntityColumns();

        for (EntityColumn entityColumn : entityColumns) {
            ReportColumn column = new ReportColumn();
            column.setEntityColumn(entityColumn);
            column.setPosition(0);

            // TODO: add modifiers?

            report.setReportColumns(asSet(column));

            runReport(report);
        }
    }

    private void testFilters(Report report, EntityProperty property)
        throws ApplicationException {
        Collection<EntityFilter> entityFilters = property
            .getEntityFilters();
        for (EntityFilter entityFilter : entityFilters) {
            ReportFilter filter = new ReportFilter();
            filter.setEntityFilter(entityFilter);
            filter.setPosition(0);

            report.setReportFilters(asSet(filter));

            testFilter(report, filter);
        }
    }

    private void testFilter(Report report, ReportFilter filter)
        throws ApplicationException {
        // TODO: use all columns?

        FilterType filterType = FilterTypes.getFilterType(filter
            .getEntityFilter().getFilterType());
        PropertyType propertyType = filter.getEntityFilter()
            .getEntityProperty().getPropertyType();

        Collection<FilterOperator> ops = filterType.getOperators();

        if (ops.isEmpty()) {
            // TODO: broken, find why and fix?
            // runReport(report);
        }

        for (FilterOperator op : ops) {
            filter.setOperator(op.getId());

            Set<ReportFilterValue> values = new HashSet<ReportFilterValue>();
            if (op.isValueRequired()) {
                ReportFilterValue value =
                    getReportFilterValue(op, propertyType);
                values.add(value);
            }

            filter.setReportFilterValues(values);

            runReport(report);
        }
    }

    private ReportFilterValue getReportFilterValue(FilterOperator op,
        PropertyType type) {
        ReportFilterValue value = new ReportFilterValue();
        value.setPosition(0);

        String name = type.getName();
        if ("String".equals(name)) {
            value.setValue("a%");
        } else if ("Number".equals(name)) {
            value.setValue("1");
            value.setSecondValue("99");
        } else if ("Date".equals(name)) {
            value.setValue(SQL_DATE_FORMAT.format(new Date(0)));
            value.setSecondValue(SQL_DATE_FORMAT.format(new Date()));
        } else if ("Boolean".equals(name)) {
            value.setValue(""); // ? probably no value required
        }

        return value;
    }

    private static <E> Set<E> asSet(E... elements) {
        return new HashSet<E>(Arrays.asList(elements));
    }

    private void runReport(Report report) throws ApplicationException {
        int maxResults = 2, firstRow = 0, timeout = 0;

        report.setIsCount(true);
        appService.runReport(new ReportInput(report), maxResults, firstRow, timeout);

        report.setIsCount(false);
        appService.runReport(new ReportInput(report), maxResults, firstRow, timeout);
    }
}
