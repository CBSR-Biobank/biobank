package edu.ualberta.med.biobank.forms.input;

import edu.ualberta.med.biobank.client.reports.AbstractReportTreeNode;
import edu.ualberta.med.biobank.client.reports.ReportTreeNode;

public class ReportInput extends FormInput {

    public ReportInput(ReportTreeNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return "";
    }

    public AbstractReportTreeNode getNode() {
        return (ReportTreeNode) getAdapter(ReportTreeNode.class);
    }

}
