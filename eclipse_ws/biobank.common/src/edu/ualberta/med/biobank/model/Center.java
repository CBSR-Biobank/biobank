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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * An abstract class that represents either a collection location, a research
 * location, or repository site. See \ref Clinic, \ref Site and \ref
 * ResearchGroup.
 */
@Entity
@Table(name = "CENTER")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR",
    discriminatorType = DiscriminatorType.STRING)
@Unique.List({
    @Unique(properties = "name", groups = PrePersist.class),
    @Unique(properties = "nameShort", groups = PrePersist.class)
})
@Empty.List({
    @Empty(property = "srcDispatchCollection", groups = PreDelete.class),
    @Empty(property = "dstDispatchCollection", groups = PreDelete.class)
})
public class Center extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String nameShort;
    private Address address = new Address();
    private Set<ProcessingEvent> processingEventCollection =
        new HashSet<ProcessingEvent>(0);
    private Set<Membership> membershipCollection =
        new HashSet<Membership>(0);
    private Set<Dispatch> srcDispatchCollection = new HashSet<Dispatch>(
        0);
    private Set<Dispatch> dstDispatchCollection = new HashSet<Dispatch>(
        0);
    private Set<OriginInfo> originInfoCollection =
        new HashSet<OriginInfo>(0);
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;
    private Set<Comment> commentCollection = new HashSet<Comment>(0);

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Center.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Center.nameShort.NotEmpty}")
    @Column(name = "NAME_SHORT", unique = true, nullable = false, length = 50)
    public String getNameShort() {
        return this.nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Center.address.NotNull}")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID", unique = true, nullable = false)
    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "center")
    public Set<ProcessingEvent> getProcessingEventCollection() {
        return this.processingEventCollection;
    }

    public void setProcessingEventCollection(
        Set<ProcessingEvent> processingEventCollection) {
        this.processingEventCollection = processingEventCollection;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", updatable = false)
    public Set<Membership> getMembershipCollection() {
        return this.membershipCollection;
    }

    public void setMembershipCollection(
        Set<Membership> membershipCollection) {
        this.membershipCollection = membershipCollection;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "senderCenter")
    public Set<Dispatch> getSrcDispatchCollection() {
        return this.srcDispatchCollection;
    }

    public void setSrcDispatchCollection(
        Set<Dispatch> srcDispatchCollection) {
        this.srcDispatchCollection = srcDispatchCollection;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "receiverCenter")
    public Set<Dispatch> getDstDispatchCollection() {
        return this.dstDispatchCollection;
    }

    public void setDstDispatchCollection(
        Set<Dispatch> dstDispatchCollection) {
        this.dstDispatchCollection = dstDispatchCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", updatable = false)
    public Set<OriginInfo> getOriginInfoCollection() {
        return this.originInfoCollection;
    }

    public void setOriginInfoCollection(
        Set<OriginInfo> originInfoCollection) {
        this.originInfoCollection = originInfoCollection;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Center.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "CENTER_COMMENT",
        joinColumns = { @JoinColumn(name = "CENTER_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getCommentCollection() {
        return this.commentCollection;
    }

    public void setCommentCollection(Set<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

}
