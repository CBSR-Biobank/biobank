package edu.ualberta.med.biobank.forms.input;

import edu.ualberta.med.biobank.common.reports.ReportTreeNode;

public class ReportInput extends FormInput {

    public ReportInput(ReportTreeNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return "";
    }

    public ReportTreeNode getNode() {
        return (ReportTreeNode) getAdapter(ReportTreeNode.class);
    }

}
