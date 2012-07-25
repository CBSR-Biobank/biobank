package edu.ualberta.med.biobank.action.scanprocess.result;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.scanprocess.CellInfoStatus;

public abstract class ProcessResult implements ActionResult {
    private static final long serialVersionUID = 1L;
    private List<String> logs = new ArrayList<String>();
    private CellInfoStatus processStatus;

    public List<String> getLogs() {
        return logs;
    }

    public CellInfoStatus getProcessStatus() {
        return processStatus;
    }

    protected void setProcessStatus(CellInfoStatus processStatus) {
        this.processStatus = processStatus;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

}
