package edu.ualberta.med.biobank.model.log;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.log.ContainerLog.LogType;

@Entity
@DiscriminatorValue("CONT")
public class ContainerLog extends Log<LogType> {
    private static final long serialVersionUID = 1L;

    private Integer containerId;

    public ContainerLog() {
    }

    public ContainerLog(Container container, LogType logType) {
        super(logType);
        setCenterId(container.getTree().getLocation().getCenter());
        setContainerId(container);
    }

    @NotNull(message = "{ContainerLog.containerId.NotNull}")
    @Column(name = "CONTAINER_ID")
    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
    }

    public void setContainerId(Container container) {
        this.containerId = (container != null) ? container.getId() : null;
    }

    public enum LogType implements ILogType {
        CREATED(1),
        READ(2),
        UPDATED(3),
        DELETED(4),
        CREATED_CHILDREN(5),
        DELETED_CHILDREN(6),
        READ_CHILD_CONTAINERS(7),
        READ_CHILD_SPECIMENS(8),
        MOVED_CHILD_CONTAINERS(9),
        MOVED_CHILD_SPECIMENS(10);

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
