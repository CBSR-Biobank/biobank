package edu.ualberta.med.biobank.model.log;

import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.log.CenterLog.LogType;

public class CenterLog extends Log<LogType> {
    private static final long serialVersionUID = 1L;

    public CenterLog() {
    }

    public CenterLog(Center center, LogType logType) {
        super(logType);
        setCenterId(center);
    }

    public enum LogType implements ILogType {
        CREATED(1),
        READ(2),
        UPDATED(3),
        DELETED(4);

        private final Integer id;

        private LogType(int id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }

    @Override
    protected Class<LogType> getLogTypeClass() {
        return LogType.class;
    }
}
