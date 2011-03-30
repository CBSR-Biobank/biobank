package edu.ualberta.med.biobank.common.scanprocess;

import java.io.Serializable;
import java.util.Map;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.util.linking.Cell;
import edu.ualberta.med.biobank.common.util.linking.CellStatus;

public class ScanProcessResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<RowColPos, Cell> cells;
    private String logs;
    private CellStatus status;

    public ScanProcessResult() {
    }

    public ScanProcessResult(Map<RowColPos, Cell> cells, String logs,
        CellStatus status) {
        super();
        this.cells = cells;
        this.logs = logs;
        this.setStatus(status);
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

    public void setStatus(CellStatus status) {
        this.status = status;
    }

    public CellStatus getStatus() {
        return status;
    }

}
