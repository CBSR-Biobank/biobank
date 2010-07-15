package edu.ualberta.med.biobank.client.reports;

import edu.ualberta.med.biobank.client.reports.advanced.QueryTreeNode;

public class AdvancedReportTreeNode extends AbstractReportTreeNode {

    QueryTreeNode query;

    public AdvancedReportTreeNode(String name, QueryTreeNode query) {
        super(name);
        this.query = query;
    }

}
