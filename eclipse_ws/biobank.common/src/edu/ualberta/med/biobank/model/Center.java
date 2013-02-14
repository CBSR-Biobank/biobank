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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * An abstract class that represents either a collection location, a research location, or
 * repository site. See \ref Clinic, \ref Site and \ref ResearchGroup.
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
    @Empty(property = "srcDispatches", groups = PreDelete.class),
    @Empty(property = "dstDispatches", groups = PreDelete.class)
})
public class Center extends AbstractBiobankModel
    implements HasName, HasNameShort, HasActivityStatus, HasComments,
    HasAddress {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Center",
        "Centers");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString DST_DISPATCHES = bundle.trc(
            "model",
            "Destination Dispatches").format();
        public static final LString SRC_DISPATCHES = bundle.trc(
            "model",
            "Source Dispatches").format();
    }

    private String name;
    private String nameShort;
    private Address address = new Address();
    private Set<ProcessingEvent> processingEvents =
        new HashSet<ProcessingEvent>(0);
    private Set<Dispatch> srcDispatches = new HashSet<Dispatch>(0);
    private Set<Dispatch> dstDispatches = new HashSet<Dispatch>(0);
    private Set<OriginInfo> originInfos = new HashSet<OriginInfo>(0);
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;
    private Set<Comment> comments = new HashSet<Comment>(0);

    @Override
    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Center.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Center.nameShort.NotEmpty}")
    @Column(name = "NAME_SHORT", unique = true, nullable = false, length = 50)
    public String getNameShort() {
        return this.nameShort;
    }

    @Override
    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.Center.address.NotNull}")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID", unique = true, nullable = false)
    public Address getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "center")
    public Set<ProcessingEvent> getProcessingEvents() {
        return this.processingEvents;
    }

    public void setProcessingEvents(Set<ProcessingEvent> processingEvents) {
        this.processingEvents = processingEvents;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "senderCenter")
    public Set<Dispatch> getSrcDispatches() {
        return this.srcDispatches;
    }

    public void setSrcDispatches(Set<Dispatch> srcDispatches) {
        this.srcDispatches = srcDispatches;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "receiverCenter")
    public Set<Dispatch> getDstDispatches() {
        return this.dstDispatches;
    }

    public void setDstDispatches(Set<Dispatch> dstDispatches) {
        this.dstDispatches = dstDispatches;
    }

    // TODO: why does this cascade exist?
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "CENTER_ID", updatable = false)
    public Set<OriginInfo> getOriginInfos() {
        return this.originInfos;
    }

    public void setOriginInfos(Set<OriginInfo> originInfos) {
        this.originInfos = originInfos;
    }

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.Center.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    @Override
    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @Override
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "CENTER_COMMENT",
        joinColumns = { @JoinColumn(name = "CENTER_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Should only be used by the Action layer.
     * 
     * @return the studies this center is associated with.
     */
    @SuppressWarnings("nls")
    @Transient
    public Set<Study> getStudiesInternal() {
        throw new IllegalStateException("should be implemented by derived classes");
    }

}
