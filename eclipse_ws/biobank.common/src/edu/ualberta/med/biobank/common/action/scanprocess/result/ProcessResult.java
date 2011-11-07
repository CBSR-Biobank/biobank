package edu.ualberta.med.biobank.common.action.scanprocess.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.scanprocess.CellInfoStatus;
import edu.ualberta.med.biobank.common.util.NotAProxy;

public abstract class ProcessResult implements Serializable, NotAProxy {

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
