package edu.ualberta.med.biobank.common.action.reports;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class ReportSaveInput implements ActionInput {
    private static final long serialVersionUID = 1L;

    private final Integer reportId;
    private final String name;
    private final String description;
    private final Integer userId;
    private final boolean isPublic;
    private final boolean isCount;
    private final Set<ReportColumnSaveInput> reportColumnInput;
    private final Integer entityId;
    private final Set<ReportFilterSaveInput> reportFilterInput;

    @SuppressWarnings("nls")
    public ReportSaveInput(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("report is null");
        }

        this.reportId = report.getId();
        this.name = report.getName();
        this.description = report.getDescription();
        this.userId = report.getUser().getId();
        this.isPublic = report.getIsPublic();
        this.isCount = report.getIsCount();
        this.entityId = report.getEntity().getId();

        this.reportColumnInput = new HashSet<ReportColumnSaveInput>(0);
        this.reportFilterInput = new HashSet<ReportFilterSaveInput>(0);

        for (ReportColumn reportColumn : report.getReportColumns()) {
            ReportColumnSaveInput input = new ReportColumnSaveInput(reportColumn);
            reportColumnInput.add(input);
        }

        for (ReportFilter reportFilter : report.getReportFilters()) {
            new ReportFilterSaveInput(reportFilter);
        }
    }

    public Integer getReportId() {
        return reportId;
    }

    public Set<ReportColumnSaveInput> getReportColumnInput() {
        return reportColumnInput;
    }

    public Set<ReportFilterSaveInput> getReportFilterInput() {
        return reportFilterInput;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getUserId() {
        return userId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isCount() {
        return isCount;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public static class ReportColumnSaveInput implements ActionInput {
        private static final long serialVersionUID = 1L;

        private final Integer position;
        private final Integer propertyModifierId;
        private final Integer entityColumnId;

        public ReportColumnSaveInput(ReportColumn reportColumn) {
            this.position = reportColumn.getPosition();
            this.propertyModifierId = reportColumn.getPropertyModifier().getId();
            this.entityColumnId = reportColumn.getEntityColumn().getId();
        }

        public Integer getPosition() {
            return position;
        }

        public Integer getPropertyModifierId() {
            return propertyModifierId;
        }

        public Integer getEntityColumnId() {
            return entityColumnId;
        }
    }

    public static class ReportFilterSaveInput implements ActionInput {
        private static final long serialVersionUID = 1L;

        private final Integer position;
        private final Integer operator;
        private final Set<ReportFilterValueSaveInput> filterValues;

        public ReportFilterSaveInput(
            Integer position,
            Integer operator,
            Set<ReportFilterValue> filterValues) {
            this.position = position;
            this.operator = operator;
            this.filterValues = new HashSet<ReportFilterValueSaveInput>(filterValues.size());
            for (ReportFilterValue filterValue : filterValues) {
                this.filterValues.add(new ReportFilterValueSaveInput(filterValue));
            }
        }

        public ReportFilterSaveInput(ReportFilter reportFilter) {
            this.position = reportFilter.getPosition();
            this.operator = reportFilter.getOperator();
            this.filterValues =
                new HashSet<ReportFilterValueSaveInput>(reportFilter.getReportFilterValues().size());
            for (ReportFilterValue filterValue : reportFilter.getReportFilterValues()) {
                this.filterValues.add(new ReportFilterValueSaveInput(filterValue));
            }
        }

        public Integer getPosition() {
            return position;
        }

        public Integer getOperator() {
            return operator;
        }

        public Set<ReportFilterValueSaveInput> getFilterValues() {
            return filterValues;
        }
    }

    public static class ReportFilterValueSaveInput implements ActionInput {
        private static final long serialVersionUID = 1L;

        private final Integer position;
        private final String value;
        private final String secondValue;

        public ReportFilterValueSaveInput(ReportFilterValue filterValue) {
            this.position = filterValue.getPosition();
            this.value = filterValue.getValue();
            this.secondValue = filterValue.getSecondValue();
        }

        public Integer getPosition() {
            return position;
        }

        public String getValue() {
            return value;
        }

        public String getSecondValue() {
            return secondValue;
        }
    }

}
