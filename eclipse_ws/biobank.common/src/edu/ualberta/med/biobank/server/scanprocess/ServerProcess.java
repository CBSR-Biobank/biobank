package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ServerProcess {
    protected WritableApplicationService appService;
    protected ProcessData data;
    protected User user;
    private List<String> logs;

    public ServerProcess(WritableApplicationService appService,
        ProcessData data, User user) {
        this.appService = appService;
        this.data = data;
        this.user = user;
        user.initCurrentWorkingCenter(appService);
        logs = new ArrayList<String>();
    }

    public ScanProcessResult processScanResult(Map<RowColPos, Cell> cells,
        boolean isRescanMode) throws Exception {
        ScanProcessResult res = getScanProcessResult(cells, isRescanMode);
        res.setLogs(logs);
        return res;
    }

    protected abstract ScanProcessResult getScanProcessResult(
        Map<RowColPos, Cell> cells, boolean isRescanMode) throws Exception;

    public CellProcessResult processCellStatus(Cell cell) throws Exception {
        CellProcessResult res = getCellProcessResult(cell);
        res.setLogs(logs);
        return res;
    }

    protected abstract CellProcessResult getCellProcessResult(Cell cell)
        throws Exception;

    public void appendNewLog(String log) {
        logs.add(log);
    }
}
