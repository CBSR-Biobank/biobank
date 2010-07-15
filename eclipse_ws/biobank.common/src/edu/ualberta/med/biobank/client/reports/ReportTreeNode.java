package edu.ualberta.med.biobank.client.reports;

public class ReportTreeNode extends AbstractReportTreeNode {
    private BiobankReport report;

    public ReportTreeNode(String name, BiobankReport report) {
        super(name);
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
