package edu.ualberta.med.biobank.server.applicationservice;

import java.io.Serializable;
import java.util.Map;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.util.linking.Cell;

public class ScanProcessResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<RowColPos, Cell> cells;
    private String logs;

    public ScanProcessResult() {
    }

    public ScanProcessResult(Map<RowColPos, Cell> cells, String logs) {
        super();
        this.cells = cells;
        this.logs = logs;
    }

    public Map<RowColPos, Cell> getCells() {
        return cells;
    }

    public String getLogs() {
        return logs;
    }

    public void setCells(Map<RowColPos, Cell> cells) {
        this.cells = cells;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

}
