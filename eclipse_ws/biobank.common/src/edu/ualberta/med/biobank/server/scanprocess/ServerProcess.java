package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.scanprocess.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.ProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.Map;

public abstract class ServerProcess {
    protected WritableApplicationService appService;
    protected ProcessData data;
    protected User user;
    protected ProcessResult res;

    public ServerProcess(WritableApplicationService appService,
        ProcessData data, User user) {
        this.appService = appService;
        this.data = data;
        this.user = user;
        user.initCurrentWorkingCenter(appService);
    }

    public void processScanResult(Map<RowColPos, Cell> cells,
        boolean isRescanMode) throws Exception {
        res = new ScanProcessResult();
        ((ScanProcessResult) res).setResult(cells,
            internalProcessScanResult(cells, isRescanMode));
    }

    public ProcessResult getProcessResult() {
        return res;
    }

    protected abstract CellStatus internalProcessScanResult(
        Map<RowColPos, Cell> cells, boolean isRescanMode) throws Exception;

    public void processCellStatus(Cell cell) throws Exception {
        res = new CellProcessResult();
        internalProcessCellStatus(cell);
        ((CellProcessResult) res).setResult(cell);
    }

    protected abstract void internalProcessCellStatus(Cell scanCell)
        throws Exception;
}
