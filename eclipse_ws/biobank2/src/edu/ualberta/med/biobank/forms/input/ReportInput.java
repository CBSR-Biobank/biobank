package edu.ualberta.med.biobank.forms.input;

import edu.ualberta.med.biobank.common.reports.AbstractReportTreeNode;
import edu.ualberta.med.biobank.common.reports.ReportTreeNode;

public class ReportInput extends FormInput {

    public ReportInput(AbstractReportTreeNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return "";
    }

    public AbstractReportTreeNode getNode() {
        return (AbstractReportTreeNode) getAdapter(ReportTreeNode.class);
    }

}
