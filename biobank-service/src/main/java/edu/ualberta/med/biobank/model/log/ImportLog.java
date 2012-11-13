package edu.ualberta.med.biobank.model.log;

import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.log.ImportLog.LogType;
import edu.ualberta.med.biobank.model.study.Study;

public class ImportLog extends Log<LogType> {
    private static final long serialVersionUID = 1L;

    public ImportLog() {
    }

    public ImportLog(Study study, Center center, LogType logType) {
        super(logType);
        setCenterId(center);
    }

    public enum LogType implements ILogType {
        ;

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
