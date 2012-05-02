package edu.ualberta.med.biobank.common.reports;

public class ReportTreeNode extends AbstractReportTreeNode {
    private final BiobankReport report;

    public ReportTreeNode(BiobankReport report) {
        super(report.getName());
        this.report = report;
    }

    @Override
    public String getToolTipText() {
        try {
            return getReport().getDescription();
        } catch (Exception e) {
        }
        return ""; //$NON-NLS-1$
    }

    public BiobankReport getReport() {
        return report;
    }

}
