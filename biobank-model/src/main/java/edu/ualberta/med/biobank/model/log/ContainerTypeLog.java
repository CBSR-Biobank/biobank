package edu.ualberta.med.biobank.model.log;

import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.log.ContainerTypeLog.LogType;

public class ContainerTypeLog extends Log<LogType> {
    private static final long serialVersionUID = 1L;

    public ContainerTypeLog() {
    }

    public ContainerTypeLog(ContainerType containerType, LogType logType) {
        super(logType);
        setCenterId(containerType.getCenter());
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
