package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

import java.util.Collection;
import java.util.HashSet;

public abstract class Center extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String nameShort;
    private Address address;
    private Collection<ProcessingEvent> processingEventCollection =
        new HashSet<ProcessingEvent>();
    private Collection<Membership> membershipCollection =
        new HashSet<Membership>();
    private Collection<Dispatch> srcDispatchCollection =
        new HashSet<Dispatch>();
    private Collection<Dispatch> dstDispatchCollection =
        new HashSet<Dispatch>();
    private Collection<OriginInfo> originInfoCollection =
        new HashSet<OriginInfo>();
    private ActivityStatus activityStatus;
    private Collection<Comment> commentCollection = new HashSet<Comment>();

    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty
    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @NotNull
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Collection<ProcessingEvent> getProcessingEventCollection() {
        return processingEventCollection;
    }

    public void setProcessingEventCollection(
        Collection<ProcessingEvent> processingEventCollection) {
        this.processingEventCollection = processingEventCollection;
    }

    public Collection<Membership> getMembershipCollection() {
        return membershipCollection;
    }

    public void setMembershipCollection(
        Collection<Membership> membershipCollection) {
        this.membershipCollection = membershipCollection;
    }

    public Collection<Dispatch> getSrcDispatchCollection() {
        return srcDispatchCollection;
    }

    public void setSrcDispatchCollection(
        Collection<Dispatch> srcDispatchCollection) {
        this.srcDispatchCollection = srcDispatchCollection;
    }

    public Collection<Dispatch> getDstDispatchCollection() {
        return dstDispatchCollection;
    }

    public void setDstDispatchCollection(
        Collection<Dispatch> dstDispatchCollection) {
        this.dstDispatchCollection = dstDispatchCollection;
    }

    public Collection<OriginInfo> getOriginInfoCollection() {
        return originInfoCollection;
    }

    public void setOriginInfoCollection(
        Collection<OriginInfo> originInfoCollection) {
        this.originInfoCollection = originInfoCollection;
    }

    @NotNull
    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }
}
