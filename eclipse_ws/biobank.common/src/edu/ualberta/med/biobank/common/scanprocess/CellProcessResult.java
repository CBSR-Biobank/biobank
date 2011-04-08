package edu.ualberta.med.biobank.common.scanprocess;

import java.io.Serializable;

public class CellProcessResult extends ProcessResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Cell cell;

    public CellProcessResult() {
        super();
    }

    public void setResult(Cell cell) {
        setProcessStatus(cell.getStatus());
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

}
