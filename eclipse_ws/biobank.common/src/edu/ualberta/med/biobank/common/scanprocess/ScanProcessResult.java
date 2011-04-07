package edu.ualberta.med.biobank.common.scanprocess;

import java.io.Serializable;
import java.util.Map;

import edu.ualberta.med.biobank.common.util.RowColPos;

public class ScanProcessResult extends ProcessResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<RowColPos, Cell> cells;

    public ScanProcessResult() {
        super();
    }

    public void setResult(Map<RowColPos, Cell> cells, CellStatus status) {
        setProcessStatus(status);
        this.cells = cells;
    }

    public Map<RowColPos, Cell> getCells() {
        return cells;
    }

}
