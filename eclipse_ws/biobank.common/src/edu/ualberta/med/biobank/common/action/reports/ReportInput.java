package edu.ualberta.med.biobank.common.action.reports;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.model.PropertyModifier;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class ReportInput implements ActionInput {
    private static final long serialVersionUID = 1L;

    private final Integer reportId;
    private final String name;
    private final String description;
    private final Integer userId;
    private final boolean isPublic;
    private final boolean isCount;
    private final Set<ReportColumnInput> reportColumnInputs;
    private final Integer entityId;
    private final Set<ReportFilterInput> reportFilterInputs;

    @SuppressWarnings("nls")
    public ReportInput(Report report) {
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

        this.reportColumnInputs = new HashSet<ReportColumnInput>(0);
        this.reportFilterInputs = new HashSet<ReportFilterInput>(0);

        for (ReportColumn reportColumn : report.getReportColumns()) {
            ReportColumnInput input = new ReportColumnInput(reportColumn);
            reportColumnInputs.add(input);
        }

        for (ReportFilter reportFilter : report.getReportFilters()) {
            ReportFilterInput input = new ReportFilterInput(reportFilter);
            reportFilterInputs.add(input);
        }
    }

    public Integer getReportId() {
        return reportId;
    }

    public Set<ReportColumnInput> getReportColumnInputs() {
        return reportColumnInputs;
    }

    public Set<ReportFilterInput> getReportFilterInputs() {
        return reportFilterInputs;
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

    public static class ReportColumnInput implements ActionInput {
        private static final long serialVersionUID = 1L;

        private final Integer position;
        private final Integer propertyModifierId;
        private final Integer entityColumnId;

        public ReportColumnInput(ReportColumn reportColumn) {
            this.position = reportColumn.getPosition();
            PropertyModifier propertyModifier = reportColumn.getPropertyModifier();
            if (propertyModifier != null) {
                this.propertyModifierId = reportColumn.getPropertyModifier().getId();
            } else {
                this.propertyModifierId = null;
            }
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

    public static class ReportFilterInput implements ActionInput {
        private static final long serialVersionUID = 1L;

        private final Integer position;
        private final Integer operator;
        private final Set<ReportFilterValueInput> filterValues;
        private final Integer entityFilterId;

        public ReportFilterInput(ReportFilter reportFilter) {
            this.position = reportFilter.getPosition();
            this.operator = reportFilter.getOperator();
            this.filterValues = new HashSet<ReportFilterValueInput>(
                reportFilter.getReportFilterValues().size());
            for (ReportFilterValue filterValue : reportFilter.getReportFilterValues()) {
                this.filterValues.add(new ReportFilterValueInput(filterValue));
            }
            this.entityFilterId = reportFilter.getEntityFilter().getId();
        }

        public Integer getPosition() {
            return position;
        }

        public Integer getOperator() {
            return operator;
        }

        public Set<ReportFilterValueInput> getFilterValueInputs() {
            return filterValues;
        }

        public Integer getEntityFilterId() {
            return entityFilterId;
        }
    }

    public static class ReportFilterValueInput implements ActionInput {
        private static final long serialVersionUID = 1L;

        private final Integer position;
        private final String value;
        private final String secondValue;

        public ReportFilterValueInput(ReportFilterValue filterValue) {
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
