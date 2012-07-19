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
public abstract class Event<T extends Enum<T>> extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private User user;
    private Long createdAt = System.currentTimeMillis();
    private T type;

    @NotNull(message = "{edu.ualberta.med.biobank.model.Event.user.NotNull")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Event.type.NotNull")
    @Column(name = "EVENT_TYPE")
    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Event.createdAt.NotNull")
    @Column(name = "CREATED_AT")
    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
