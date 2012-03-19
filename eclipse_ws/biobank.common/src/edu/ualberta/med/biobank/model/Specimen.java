package edu.ualberta.med.biobank.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * caTissue Term - Aliquot: Pertaining to a portion of the whole; any one of two
 * or more samples of something, of the same volume or weight.
 * 
 * NCI Term - Specimen: A part of a thing, or of several things, taken to
 * demonstrate or to determine the character of the whole, e.g. a substance, or
 * portion of material obtained for use in testing, examination, or study;
 * particularly, a preparation of tissue or bodily fluid taken for examination
 * or diagnosis.
 */
@Entity
@Table(name = "SPECIMEN")
@Unique(properties = "inventoryId", groups = PrePersist.class)
@Empty(property = "childSpecimens", groups = PreDelete.class)
@NotUsed(by = DispatchSpecimen.class, property = "specimen", groups = PreDelete.class)
public class Specimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String inventoryId;
    private BigDecimal quantity;
    private Date createdAt;
    private Specimen topSpecimen = this;
    private CollectionEvent collectionEvent;
    private Center currentCenter;
    private Set<DispatchSpecimen> dispatchSpecimens =
        new HashSet<DispatchSpecimen>(0);
    private CollectionEvent originalCollectionEvent;
    private SpecimenType specimenType;
    private SpecimenPosition specimenPosition;
    private Set<Specimen> childSpecimens = new HashSet<Specimen>(0);
    private Set<Comment> comments = new HashSet<Comment>(0);
    private Set<RequestSpecimen> requestSpecimens =
        new HashSet<RequestSpecimen>(0);
    private OriginInfo originInfo;
    @Enumerated(EnumType.STRING)
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;
    private ProcessingEvent processingEvent;
    private Specimen parentSpecimen;

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Specimen.inventoryId.NotEmpty}")
    @Column(name = "INVENTORY_ID", unique = true, nullable = false, length = 100)
    public String getInventoryId() {
        return this.inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    @Digits(integer = 10, fraction = 10, message = "{edu.ualberta.med.biobank.model.Specimen.quantity.Digits}")
    @Column(name = "QUANTITY", precision = 10, scale = 10)
    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Specimen.createdAt.NotNull}")
    @Column(name = "CREATED_AT", nullable = false)
    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP_SPECIMEN_ID")
    public Specimen getTopSpecimen() {
        return this.topSpecimen;
    }

    public void setTopSpecimen(Specimen topSpecimen) {
        this.topSpecimen = topSpecimen;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Specimen.collectionEvent.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTION_EVENT_ID", nullable = false)
    public CollectionEvent getCollectionEvent() {
        return this.collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Specimen.currentCenter.NotNull}")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CURRENT_CENTER_ID", nullable = false)
    public Center getCurrentCenter() {
        return this.currentCenter;
    }

    public void setCurrentCenter(Center currentCenter) {
        this.currentCenter = currentCenter;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "specimen")
    public Set<DispatchSpecimen> getDispatchSpecimens() {
        return this.dispatchSpecimens;
    }

    public void setDispatchSpecimens(Set<DispatchSpecimen> dispatchSpecimens) {
        this.dispatchSpecimens = dispatchSpecimens;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORIGINAL_COLLECTION_EVENT_ID")
    public CollectionEvent getOriginalCollectionEvent() {
        return this.originalCollectionEvent;
    }

    public void setOriginalCollectionEvent(
        CollectionEvent originalCollectionEvent) {
        this.originalCollectionEvent = originalCollectionEvent;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Specimen.specimenType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false)
    public SpecimenType getSpecimenType() {
        return this.specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "specimen")
    public SpecimenPosition getSpecimenPosition() {
        return this.specimenPosition;
    }

    public void setSpecimenPosition(SpecimenPosition specimenPosition) {
        this.specimenPosition = specimenPosition;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentSpecimen")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<Specimen> getChildSpecimens() {
        return this.childSpecimens;
    }

    public void setChildSpecimens(Set<Specimen> childSpecimens) {
        this.childSpecimens = childSpecimens;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SPECIMEN_COMMENT",
        joinColumns = { @JoinColumn(name = "SPECIMEN_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", updatable = false)
    public Set<RequestSpecimen> getRequestSpecimens() {
        return this.requestSpecimens;
    }

    public void setRequestSpecimens(Set<RequestSpecimen> requestSpecimens) {
        this.requestSpecimens = requestSpecimens;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Specimen.originInfo.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORIGIN_INFO_ID", nullable = false)
    public OriginInfo getOriginInfo() {
        return this.originInfo;
    }

    public void setOriginInfo(OriginInfo originInfo) {
        this.originInfo = originInfo;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Specimen.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSING_EVENT_ID")
    public ProcessingEvent getProcessingEvent() {
        return this.processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_SPECIMEN_ID")
    public Specimen getParentSpecimen() {
        return this.parentSpecimen;
    }

    public void setParentSpecimen(Specimen parentSpecimen) {
        this.parentSpecimen = parentSpecimen;
    }
}
