package edu.ualberta.med.biobank.forms.input;

import edu.ualberta.med.biobank.common.reports.AbstractReportTreeNode;
import edu.ualberta.med.biobank.common.util.StringUtil;

public class ReportInput extends FormInput {

    public ReportInput(AbstractReportTreeNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return StringUtil.EMPTY_STRING;
    }

    public Object getNode() {
        return obj;
    }
}
