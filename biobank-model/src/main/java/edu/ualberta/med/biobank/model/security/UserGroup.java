package edu.ualberta.med.biobank.model.security;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.LongIdModel;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "USER_GROUP",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "USER_ID", "GROUP_ID" })
    })
@Unique(properties = { "user", "group" }, groups = PrePersist.class)
public class UserGroup
    extends LongIdModel {
    private static final long serialVersionUID = 1L;

    private User user;
    private Group group;

    @NotNull(message = "{UserGroup.user.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NotNull(message = "{UserGroup.group.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
