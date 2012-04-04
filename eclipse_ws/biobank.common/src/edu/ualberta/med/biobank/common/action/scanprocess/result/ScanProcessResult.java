package edu.ualberta.med.biobank.common.action.scanprocess.result;

import java.io.Serializable;
import java.util.Map;

import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfoStatus;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class ScanProcessResult extends ProcessResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<RowColPos, CellInfo> cells;

    public ScanProcessResult() {
        super();
    }

    public void setResult(Map<RowColPos, CellInfo> cells, CellInfoStatus status) {
        setProcessStatus(status);
        this.cells = cells;
    }

    public Map<RowColPos, CellInfo> getCells() {
        return cells;
    }

}
