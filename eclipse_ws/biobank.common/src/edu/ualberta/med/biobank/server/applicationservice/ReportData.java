package edu.ualberta.med.biobank.server.applicationservice;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.reports.ReportInput;

public class ReportData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ReportInput reportInput;
    private int maxResults;
    private int timeout;
    private int firstRow;

    public ReportData(ReportInput reportInput) {
        this.reportInput = reportInput;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeoutInSeconds) {
        this.timeout = timeoutInSeconds;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    public ReportInput getReportInput() {
        return reportInput;
    }
}
