package edu.ualberta.med.biobank.common.reports;

public class ReportTreeNode extends AbstractReportTreeNode {
    private BiobankReport report;

    public ReportTreeNode(BiobankReport report) {
        super(report.getName());
        this.report = report;
    }

    public String getToolTipText() {
        try {
            return getReport().getDescription();
        } catch (Exception e) {
        }
        return "";
    }

    public BiobankReport getReport() {
        return report;
    }

}
