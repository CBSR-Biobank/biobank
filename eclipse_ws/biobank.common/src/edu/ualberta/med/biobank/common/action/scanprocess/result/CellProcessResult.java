package edu.ualberta.med.biobank.common.action.scanprocess.result;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;

public class CellProcessResult extends ProcessResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private CellInfo cell;

    public CellProcessResult() {
        super();
    }

    public void setResult(CellInfo cell) {
        setProcessStatus(cell.getStatus());
        this.cell = cell;
    }

    public CellInfo getCell() {
        return cell;
    }

}
