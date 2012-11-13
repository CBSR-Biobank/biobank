package edu.ualberta.med.biobank.model.log;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.log.CollectionEventLog.LogType;
import edu.ualberta.med.biobank.model.study.CollectionEvent;

public class CollectionEventLog extends Log<LogType> {
    private static final long serialVersionUID = 1L;

    private Integer collectionEventId;

    public CollectionEventLog() {
    }

    public CollectionEventLog(CollectionEvent collectionEvent, LogType logType) {
        super(logType);
        setStudyId(collectionEvent.getPatient().getStudy());
        setCollectionEventId(collectionEvent);
    }

    @NotNull(message = "{CollectionEventLog.collectionEventId.NotNull}")
    @Column(name = "COLLECTION_EVENT_ID")
    public Integer getCollectionEventId() {
        return collectionEventId;
    }

    public void setCollectionEventId(Integer collectionEventId) {
        this.collectionEventId = collectionEventId;
    }

    public void setCollectionEventId(CollectionEvent collectionEvent) {
        this.collectionEventId = (collectionEvent != null)
            ? collectionEvent.getId()
            : null;
    }

    public enum LogType implements ILogType {
        CREATED(1),
        READ(2),
        UPDATED(3),
        DELETED(4),
        READ_ATTRIBUTES(5),
        READ_ALIQUOTED_SPECIMENS(6),
        READ_SOURCE_SPECIMENS(7);

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
