package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.client.reports.advanced.QueryTreeNode;

public class AdvancedReportTreeNode extends AbstractReportTreeNode {

    protected QueryTreeNode query;

    public AdvancedReportTreeNode(String name, QueryTreeNode query) {
        super(name);
        this.query = query;
    }

    public QueryTreeNode getQueryTreeNode() {
        return query;
    }

}
