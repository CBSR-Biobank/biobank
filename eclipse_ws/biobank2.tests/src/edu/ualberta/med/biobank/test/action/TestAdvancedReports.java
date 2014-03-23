package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportSaveAction;
import edu.ualberta.med.biobank.common.action.reports.ReportSaveInput;
import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.PropertyModifier;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class TestAdvancedReports extends TestAction {

    private ReportColumn getReportColumn(
        Integer position,
        PropertyModifier propertyModifier,
        String entityColumnName) {
        EntityColumn entityColumn = (EntityColumn) session.createCriteria(EntityColumn.class)
            .add(Restrictions.eq("name", entityColumnName))
            .uniqueResult();

        Assert.assertNotNull("entity column is null", entityColumn);

        ReportColumn column = new ReportColumn();
        column.setPosition(position);
        column.setPropertyModifier(propertyModifier);
        column.setEntityColumn(entityColumn);
        return column;
    }

    private ReportFilter getReportFilter(
        Integer position,
        FilterOperator operator,
        Set<ReportFilterValue> reportFilterValues,
        EntityFilter entityFilter) {
        ReportFilter filter = new ReportFilter();
        filter.setPosition(position);
        filter.setOperator(operator.getId());
        filter.getReportFilterValues().addAll(reportFilterValues);
        filter.setEntityFilter(entityFilter);
        return filter;
    }

    private ReportFilterValue createReportFilterValue(
        Integer position,
        String value,
        String secondValue) {

        ReportFilterValue filterValue = new ReportFilterValue();
        filterValue.setPosition(position);
        filterValue.setValue(value);
        filterValue.setSecondValue(secondValue);

        return filterValue;
    }

    private Report createReport(String name, String description) {
        Entity entity = (Entity) session.createCriteria(Entity.class)
            .add(Restrictions.eq("name", "Specimen"))
            .uniqueResult();

        Assert.assertNotNull("entity is null", entity);

        EntityFilter entityFilter = (EntityFilter)
            session.createCriteria(EntityFilter.class, "entityFilter")
                .createAlias("entityFilter.entityProperty", "entityProperty")
                .add(Restrictions.eq("name", "Inventory Id"))
                .add(Restrictions.eq("entityProperty.property", "inventoryId"))
                .uniqueResult();

        Assert.assertNotNull("entity filter is null", entityFilter);

        Report report = new Report();

        report.setName(name);
        report.setDescription(description);
        report.setUser(getGlobalAdmin());
        report.setIsPublic(false);
        report.setIsCount(false);
        report.setEntity(entity);
        report.getReportColumns().add(getReportColumn(0, null, "Inventory Id"));

        Set<ReportFilterValue> filterValues = new HashSet<ReportFilterValue>();
        filterValues.add(createReportFilterValue(0, "abcdef", null));

        report.getReportFilters().add(getReportFilter(
            0, FilterOperator.EQUALS, filterValues, entityFilter));

        return report;
    }

    @Test
    public void saveNew() {
        String name = getMethodNameR();
        String description = getMethodNameR();
        Report report = createReport(name, description);

        IdResult idResult = exec(new AdvancedReportSaveAction(new ReportSaveInput(report)));

        Report dbReport = (Report) session.load(Report.class, idResult.getId());

        Assert.assertEquals(report.getName(), dbReport.getName());
        Assert.assertEquals(report.getDescription(), dbReport.getDescription());
        Assert.assertEquals(report.getIsPublic(), dbReport.getIsPublic());
        Assert.assertEquals(report.getIsCount(), dbReport.getIsCount());
        Assert.assertEquals(report.getEntity().getId(), dbReport.getEntity().getId());

        @SuppressWarnings("unchecked")
        List<ReportFilter> dbColumns = session.createCriteria(ReportColumn.class, "reportColumn")
            .createAlias("reportColumn.report", "report")
            .add(Restrictions.eq("report.id", idResult.getId()))
            .list();

        Assert.assertEquals(report.getReportColumns().size(), dbColumns.size());

        @SuppressWarnings("unchecked")
        List<ReportFilter> dbFilters = session.createCriteria(ReportFilter.class, "reportFilter")
            .createAlias("reportFilter.report", "report")
            .add(Restrictions.eq("report.id", idResult.getId()))
            .list();

        Assert.assertEquals(report.getReportFilters().size(), dbFilters.size());

        for (ReportFilter filter : dbFilters) {
            Assert.assertEquals(1, filter.getReportFilterValues().size());
        }
    }

    @Test
    public void resave() {
        String name = getMethodNameR();
        Report report = createReport(name, name);
        IdResult idResult = exec(new AdvancedReportSaveAction(new ReportSaveInput(report)));

        String name2 = getMethodNameR();
        report = (Report) session.load(Report.class, idResult.getId());
        report.setName(name2);
        IdResult idResult2 = exec(new AdvancedReportSaveAction(new ReportSaveInput(report)));

        Assert.assertEquals(idResult.getId(), idResult2.getId());

        Report dbReport = (Report) session.load(Report.class, idResult.getId());
        Assert.assertEquals(name2, dbReport.getName());

        @SuppressWarnings("unchecked")
        List<ReportFilter> dbColumns = session.createCriteria(ReportColumn.class, "reportColumn")
            .createAlias("reportColumn.report", "report")
            .add(Restrictions.eq("report.id", idResult.getId()))
            .list();

        Assert.assertEquals(report.getReportColumns().size(), dbColumns.size());

        @SuppressWarnings("unchecked")
        List<ReportFilter> dbFilters = session.createCriteria(ReportFilter.class, "reportFilter")
            .createAlias("reportFilter.report", "report")
            .add(Restrictions.eq("report.id", idResult.getId()))
            .list();

        Assert.assertEquals(report.getReportFilters().size(), dbFilters.size());
    }
}
