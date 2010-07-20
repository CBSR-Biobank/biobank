package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.client.reports.advanced.QueryTreeNode;

public class AdvancedReportTreeNode extends AbstractReportTreeNode {

    protected QueryTreeNode query;

    public AdvancedReportTreeNode(String name, QueryTreeNode query) {
        super(name);
        this.query = query;
    }

    @Override
    public String getToolTipText() {
        try {
            return getQueryTreeNode().getLabel();
        } catch (Exception e) {
        }
        return "";
    }

    public QueryTreeNode getQueryTreeNode() {
        return query;
    }

}
