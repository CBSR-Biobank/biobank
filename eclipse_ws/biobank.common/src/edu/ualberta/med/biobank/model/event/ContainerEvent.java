package edu.ualberta.med.biobank.model.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.event.ContainerEvent.ContainerEventType;
import edu.ualberta.med.biobank.model.util.EnumUserType;

@Entity
@DiscriminatorValue("2")
public class ContainerEvent
    extends CenterEvent<ContainerEventType> {
    private static final long serialVersionUID = 1L;

    private EntityId container;

    public ContainerEvent(Container container) {
        setCenter(container.getSite());
        setContainer(new EntityId(container.getLabel(), container));
    }

    @Type(type = "edu.ualberta.med.biobank.model.util.EnumUserType",
        parameters = @Parameter(name = EnumUserType.ENUM_CLASS_NAME_PARAM,
            value = "edu.ualberta.med.biobank.model.event.ContainerEvent$ContainerEventType"))
    @Override
    public ContainerEventType getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(ContainerEventType eventType) {
        this.eventType = eventType;
    }

    @OneToOne
    @JoinTable(name = "EVENT_CONTAINER",
        joinColumns = @JoinColumn(name = "EVENT_ID", unique = true))
    public EntityId getContainer() {
        return container;
    }

    public void setContainer(EntityId container) {
        this.container = container;
    }

    public enum ContainerEventType implements EventType {
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

        @Override
        public Integer getId() {
            return id;
        }
    }
}
