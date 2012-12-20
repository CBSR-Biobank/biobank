package edu.ualberta.med.biobank.reports;

import java.io.Serializable;

public class QueryHandleRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum CommandType {
        CREATE,
        START,
        STOP;
    }

    private QueryCommand qc;
    private QueryHandle qh;
    private CommandType ct;

    // FIXME: member appService is not serializable
    private BiobankApplicationService appService;

    public QueryHandleRequest(QueryCommand qc, CommandType ct, QueryHandle qh,
        BiobankApplicationService appService) {
        this.qc = qc;
        this.ct = ct;
        this.qh = qh;
        this.appService = appService;
    }

    public QueryCommand getQueryCommand() {
        return qc;
    }

    public CommandType getCommandType() {
        return ct;
    }

    public QueryHandle getQueryHandle() {
        return qh;
    }

    public BiobankApplicationService getAppService() {
        return appService;
    }

}
