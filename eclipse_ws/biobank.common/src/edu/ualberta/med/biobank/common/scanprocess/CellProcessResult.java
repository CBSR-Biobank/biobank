package edu.ualberta.med.biobank.common.scanprocess;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.util.linking.CellStatus;

public class CellProcessResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private CellStatus status;
    private String logs;

    public CellProcessResult() {
    }

    public CellProcessResult(CellStatus status, String logs) {
        super();
        this.setStatus(status);
        this.logs = logs;
    }

    public String getLogs() {
        return logs;
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
