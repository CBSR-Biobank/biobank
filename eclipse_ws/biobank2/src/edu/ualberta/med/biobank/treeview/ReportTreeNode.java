package edu.ualberta.med.biobank.treeview;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.client.reports.AbstractReport;

public class ReportTreeNode extends Object {
    private ReportTreeNode parent;
    private List<ReportTreeNode> children;
    private Class<? extends AbstractReport> reportClass;
    private String name;

    public ReportTreeNode(String name,
        Class<? extends AbstractReport> reportClass) {
        this.name = name;
        this.reportClass = reportClass;
        this.children = new ArrayList<ReportTreeNode>();
    }

    public String getLabel() {
        return name;
    }

    public String getToolTipText() {
        try {
            if (AbstractReport.class.isAssignableFrom(reportClass)) {
                Constructor<?> c = ((Class<?>) reportClass).getConstructor();
                AbstractReport report = (AbstractReport) c.newInstance();
                return report.getDescription();

            }
        } catch (Exception e) {
        }
        return "";
    }

    public Class<? extends AbstractReport> getReportClass() {
        return reportClass;
    }

    public ReportTreeNode getParent() {
        return parent;
    }

    public List<ReportTreeNode> getChildren() {
        return children;
    }

    public boolean isRoot() {
        return (parent == null);
    }

    public boolean isLeaf() {
        return (children.size() == 0);
    }

    public void addChild(ReportTreeNode n) {
        children.add(n);
    }

    public void removeChild(ReportTreeNode n) {
        children.remove(n);
    }

    public void setParent(ReportTreeNode n) {
        parent = n;
    }

}
