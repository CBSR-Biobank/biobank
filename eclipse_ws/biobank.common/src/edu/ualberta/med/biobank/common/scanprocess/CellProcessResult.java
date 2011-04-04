package edu.ualberta.med.biobank.common.scanprocess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CellProcessResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> logs;
    private CellStatus status;

    public CellProcessResult() {
        logs = new ArrayList<String>();
    }

    public void setResult(CellStatus status) {
        this.status = status;
    }

    public List<String> getLogs() {
        return logs;
    }

    public CellStatus getStatus() {
        return status;
    }

    public void appendNewLog(String log) {
        logs.add(log);
    }

}
