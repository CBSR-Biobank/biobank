package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "PRINCIPAL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR",
    discriminatorType = DiscriminatorType.STRING)
public class Principal extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Set<Membership> memberships = new HashSet<Membership>(0);
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;

    // Require at least one membership on creation so there is some loose
    // association between the creator and the created user.
    // FIXME: move this to group and require at least one group or membership
    // for a user.
    // @NotEmpty(groups = PreInsert.class, message =
    // "{edu.ualberta.med.biobank.model.Principal.memberships.NotEmpty}")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "principal")
    public Set<Membership> getMemberships() {
        return this.memberships;
    }

    public void setMemberships(Set<Membership> memberships) {
        this.memberships = memberships;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Principal.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    /**
     * Return true if this {@link Principal} can be removed by the given
     * {@link User}, i.e. if the given {@link User} is of <em>equal</em> or
     * greater power.
     * 
     * @param user
     * @return true if this {@link Principal} is subordinate to the given
     *         {@link User}.
     */
    @Transient
    public boolean isFullyManageable(User user) {
        for (Membership membership : getMemberships()) {
            if (!membership.isFullyManageable(user)) return false;
        }
        return true;
    }
}
