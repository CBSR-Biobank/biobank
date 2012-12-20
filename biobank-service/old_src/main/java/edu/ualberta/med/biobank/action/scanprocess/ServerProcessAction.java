package edu.ualberta.med.biobank.action.scanprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.util.RowColPos;

public abstract class ServerProcessAction implements Action<ProcessResult> {
    private static final long serialVersionUID = 1L;

    protected Integer currentWorkingCenterId;
    private List<String> logs;
    protected Locale locale;
    private Map<RowColPos, CellInfo> cells;
    private boolean isRescanMode = false;
    private boolean processOneCell;
    private CellInfo cell;

    protected User user;
    protected Session session;
    protected ActionContext actionContext;

    public ServerProcessAction(
        Integer currentWorkingCenterId,
        Map<RowColPos, CellInfo> cells,
        boolean isRescanMode, Locale locale) {
        this.currentWorkingCenterId = currentWorkingCenterId;
        this.cells = cells;
        this.isRescanMode = isRescanMode;
        this.locale = locale;
        this.processOneCell = false;
        logs = new ArrayList<String>();
    }

    public ServerProcessAction(
        Integer currentWorkingCenterId,
        CellInfo cell,
        Locale locale) {
        this.currentWorkingCenterId = currentWorkingCenterId;
        this.cell = cell;
        this.locale = locale;
        this.processOneCell = true;
        logs = new ArrayList<String>();
    }

    @Override
    public ProcessResult run(ActionContext context)
        throws ActionException {
        ProcessResult res;

        this.user = context.getUser();
        this.session = context.getSession();
        this.actionContext = new ActionContext(user, session, null);

        if (processOneCell)
            res = getCellProcessResult(cell);
        else
            res = getScanProcessResult(cells, isRescanMode);
        res.setLogs(logs);
        return res;
    }

    protected abstract ScanProcessResult getScanProcessResult(
        Map<RowColPos, CellInfo> cells, boolean isRescanMode)
        throws ActionException;

    protected abstract CellProcessResult getCellProcessResult(CellInfo cell)
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
