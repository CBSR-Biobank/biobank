package edu.ualberta.med.biobank.common.action.scanprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public abstract class ServerProcess implements Action<ProcessResult> {
    private static final long serialVersionUID = 1L;

    protected Integer currentWorkingCenterId;
    private List<String> logs;
    protected Locale locale;
    private Map<RowColPos, Cell> cells;
    private boolean isRescanMode = false;
    private boolean processOneCell;
    private Cell cell;

    public ServerProcess(
        Integer currentWorkingCenterId,
        Map<RowColPos, Cell> cells,
        boolean isRescanMode, Locale locale) {
        this.currentWorkingCenterId = currentWorkingCenterId;
        this.cells = cells;
        this.isRescanMode = isRescanMode;
        this.locale = locale;
        this.processOneCell = false;
        logs = new ArrayList<String>();
    }

    public ServerProcess(
        Integer currentWorkingCenterId,
        Cell cell,
        Locale locale) {
        this.currentWorkingCenterId = currentWorkingCenterId;
        this.cell = cell;
        this.locale = locale;
        this.processOneCell = true;
        logs = new ArrayList<String>();
    }

    @Override
    public ProcessResult run(User user, Session session)
        throws ActionException {
        ProcessResult res;
        if (processOneCell)
            res = getCellProcessResult(session, cell);
        else
            res = getScanProcessResult(session, cells, isRescanMode);
        res.setLogs(logs);
        return res;
    }

    protected abstract ScanProcessResult getScanProcessResult(Session session,
        Map<RowColPos, Cell> cells, boolean isRescanMode)
        throws ActionException;

    protected abstract CellProcessResult getCellProcessResult(Session session,
        Cell cell)
        throws ActionException;

    public void appendNewLog(String log) {
        logs.add(log);
    }

    protected Specimen searchSpecimen(Session session, String value) {
        Criteria criteria = session.createCriteria(Specimen.class).add(
            Restrictions.eq(SpecimenPeer.INVENTORY_ID.getName(),
                value));
        @SuppressWarnings("unchecked")
        List<Specimen> list = criteria.list();
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }
}
