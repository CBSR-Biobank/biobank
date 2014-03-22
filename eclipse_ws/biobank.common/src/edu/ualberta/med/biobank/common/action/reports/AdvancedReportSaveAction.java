package edu.ualberta.med.biobank.common.action.reports;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.reports.ReportSaveInput.ReportColumnSaveInput;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.model.User;

public class AdvancedReportSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private final ReportSaveInput info;

    public AdvancedReportSaveAction(ReportSaveInput input) {
        this.info = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return false;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Report report = context.load(Report.class, info.getReportId(), new Report());

        
        report.setName(info.getName()); 
        report.setDescription(info.getDescription());
        report.setIsCount(info.isCount()); 
        report.setIsPublic(info.isPublic());
        
        Entity entity = context.load(Entity.class, info.getEntityId());
        report.setEntity(entity);
        
        User user = context.load(User.class, info.getUserId());
        report.setUser(user);
        
        Set<ReportColumn> reportColumns = new HashSet<ReportColumn>(); 
        for (ReportColumnSaveInput input : info.getReportColumnInput()) {
            
            ReportColumn columnCopy = new ReportColumn();
            columnCopy.setEntityColumn(column.getEntityColumn());
        columnCopy.setPosition(column.getPosition());
        columnCopy.setPropertyModifier(column.getPropertyModifier());
                reportColumns.add(columnCopy); 
                } 
        report.setReportColumns(reportColumns);
        
        Set<ReportFilter> reportFilters = new HashSet<ReportFilter>(); for (ReportFilter filter :
        notNull(info.reportFilters) { ReportFilter filterCopy = new ReportFilter();
        filterCopy.setEntityFilter(filter.getEntityFilter());
        filterCopy.setOperator(filter.getOperator());
        filterCopy.setPosition(filter.getPosition());
        
        Set<ReportFilterValue> values = new HashSet<ReportFilterValue>(); for (ReportFilterValue
        value : notNull(filter .getReportFilterValues())) { ReportFilterValue valueCopy = new
        ReportFilterValue(); valueCopy.setPosition(value.getPosition());
        valueCopy.setValue(value.getValue()); valueCopy.setSecondValue(value.getSecondValue());
        
        values.add(valueCopy); } filterCopy.setReportFilterValues(values);
        
        reportFilters.add(filterCopy); } report.setReportFilters(reportFilters);
        
        return new IdResult(2);
    }
}
