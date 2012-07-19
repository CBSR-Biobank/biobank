package edu.ualberta.med.biobank.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;

import edu.ualberta.med.biobank.model.Container;

@Entity
@DiscriminatorValue("2")
public class ContainerEvent extends CenterEvent {
    private static final long serialVersionUID = 1L;

    private EventObject container;

    public ContainerEvent(Container container) {
        setCenter(container.getSite());
        setContainer(new EventObject());
    }

    @OneToOne
    @JoinTable(name = "CONTAINER_EVENT",
        joinColumns = @JoinColumn(name = "CONTAINER_EVENT_ID", unique = true))
    public EventObject getContainer() {
        return container;
    }

    public void setContainer(EventObject container) {
        this.container = container;
    }

    // TODO: implement interface for really easy usertype?
    public enum ContainerEventType {
        CREATE(1),
        READ(2),
        UPDATE(3),
        DELETE(4),
        CREATE_CHILDREN(5),
        DELETE_CHILDREN(6),
        READ_CHILDREN(7),
        READ_BY_LABEL(8),
        READ_SPECIMENS(9);

        private final Integer id;

        private ContainerEventType(int id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }
    }
}
