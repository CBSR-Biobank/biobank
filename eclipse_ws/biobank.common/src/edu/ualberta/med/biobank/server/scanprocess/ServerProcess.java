package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.scanprocess.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.Map;

public abstract class ServerProcess {
    protected WritableApplicationService appService;
    protected ProcessData data;
    protected User user;

    public ServerProcess(WritableApplicationService appService,
        ProcessData data, User user) {
        this.appService = appService;
        this.data = data;
        this.user = user;
        user.initCurrentWorkingCenter(appService);
    }

    public ScanProcessResult processScanResult(Map<RowColPos, Cell> cells,
        boolean isRescanMode) throws Exception {
        ScanProcessResult res = new ScanProcessResult();
        res.setResult(cells,
            internalProcessScanResult(res, cells, isRescanMode));
        return res;
    }

    protected abstract CellStatus internalProcessScanResult(
        ScanProcessResult res, Map<RowColPos, Cell> cells, boolean isRescanMode)
        throws Exception;

    public abstract CellProcessResult processCellStatus(Cell cell)
        throws Exception;

}
