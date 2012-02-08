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
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PRINCIPAL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR",
    discriminatorType = DiscriminatorType.STRING)
public class Principal extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Set<Membership> membershipCollection =
        new HashSet<Membership>(0);
    private ActivityStatus activityStatus;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "principal")
    public Set<Membership> getMembershipCollection() {
        return this.membershipCollection;
    }

    public void setMembershipCollection(
        Set<Membership> membershipCollection) {
        this.membershipCollection = membershipCollection;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Principal.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }
}
