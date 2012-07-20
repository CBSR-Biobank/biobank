package edu.ualberta.med.biobank.model.event;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.AbstractModel;
import edu.ualberta.med.biobank.model.User;

@Entity
@Table(name = "EVENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.INTEGER)
@SuppressWarnings("nls")
public abstract class Event<T extends Enum<T> & EventType>
    extends AbstractModel
    implements HasEventType<T> {
    private static final long serialVersionUID = 1L;

    protected static final String CENTER_COLUMN_NAME = "CENTER_ID";
    protected static final String STUDY_COLUMN_NAME = "STUDY_ID";
    protected static final String EVENT_TYPE_COLUMN_NAME = "EVENT_TYPE_ID";
    protected static final String EVENT_TYPE_NOT_NULL_MESSAGE =
        "{edu.ualberta.med.biobank.model.Event.type.NotNull";

    private User user;
    private Long createdAt = System.currentTimeMillis();
    protected T eventType;

    @NotNull(message = "{edu.ualberta.med.biobank.model.Event.user.NotNull")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Event.createdAt.NotNull")
    @Column(name = "CREATED_AT")
    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public static class SpecimenTypeReadEvent extends Event {
        private static final long serialVersionUID = 1L;

        @Override
        public Enum getEventType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setEventType(Enum eventType) {
            // TODO Auto-generated method stub

        }

    }
}
