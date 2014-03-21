package edu.ualberta.med.biobank.common.action.reports;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;

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
            ReportColumnSaveInput input = new ReportColumnSaveInput(
                reportColumn.getPosition(),
                reportColumn.getPropertyModifier().getId(),
                reportColumn.getEntityColumn().getId());
            reportColumnInput.add(input);
        }
    }

    public Integer getReportId() {
        return reportId;
    }

    public Set<ReportColumnSaveInput> getReportColumnIds() {
        return reportColumnInput;
    }

    public Set<ReportFilterSaveInput> getReportFilterIds() {
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

        public ReportColumnSaveInput(
            Integer position,
            Integer propertyModifieerId,
            Integer entityColumnId) {
            this.position = position;
            this.propertyModifierId = propertyModifieerId;
            this.entityColumnId = entityColumnId;

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

        public ReportFilterSaveInput() {

        }
    }
}
