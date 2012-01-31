package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Collection;
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

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "CENTER")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR",
    discriminatorType = DiscriminatorType.STRING)
public class Center extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String nameShort;
    private Address address;
    private Collection<ProcessingEvent> processingEventCollection =
        new HashSet<ProcessingEvent>(0);
    private Collection<Membership> membershipCollection =
        new HashSet<Membership>(0);
    private Collection<Dispatch> srcDispatchCollection = new HashSet<Dispatch>(
        0);
    private Collection<Dispatch> dstDispatchCollection = new HashSet<Dispatch>(
        0);
    private Collection<OriginInfo> originInfoCollection =
        new HashSet<OriginInfo>(0);
    private ActivityStatus activityStatus;
    private Set<Comment> commentCollection = new HashSet<Comment>(0);

    @NotEmpty
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty
    @Column(name = "NAME_SHORT", unique = true, nullable = false, length = 50)
    public String getNameShort() {
        return this.nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID", unique = true, nullable = false)
    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "center")
    public Collection<ProcessingEvent> getProcessingEventCollection() {
        return this.processingEventCollection;
    }

    public void setProcessingEventCollection(
        Collection<ProcessingEvent> processingEventCollection) {
        this.processingEventCollection = processingEventCollection;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", updatable = false)
    public Collection<Membership> getMembershipCollection() {
        return this.membershipCollection;
    }

    public void setMembershipCollection(
        Collection<Membership> membershipCollection) {
        this.membershipCollection = membershipCollection;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "senderCenter")
    public Collection<Dispatch> getSrcDispatchCollection() {
        return this.srcDispatchCollection;
    }

    public void setSrcDispatchCollection(
        Collection<Dispatch> srcDispatchCollection) {
        this.srcDispatchCollection = srcDispatchCollection;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "receiverCenter")
    public Collection<Dispatch> getDstDispatchCollection() {
        return this.dstDispatchCollection;
    }

    public void setDstDispatchCollection(
        Collection<Dispatch> dstDispatchCollection) {
        this.dstDispatchCollection = dstDispatchCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", updatable = false)
    public Collection<OriginInfo> getOriginInfoCollection() {
        return this.originInfoCollection;
    }

    public void setOriginInfoCollection(
        Collection<OriginInfo> originInfoCollection) {
        this.originInfoCollection = originInfoCollection;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_STATUS_ID", nullable = false)
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
