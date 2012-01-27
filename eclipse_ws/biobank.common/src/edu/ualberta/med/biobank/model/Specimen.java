package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.NotEmpty;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Specimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String inventoryId;
    private Collection<Comment> commentCollection = new HashSet<Comment>();
    private Double quantity;
    private Date createdAt;
    private Collection<RequestSpecimen> requestSpecimenCollection =
        new HashSet<RequestSpecimen>();
    private ActivityStatus activityStatus;
    private Collection<Specimen> childSpecimenCollection =
        new HashSet<Specimen>();
    private OriginInfo originInfo;
    private Collection<DispatchSpecimen> dispatchSpecimenCollection =
        new HashSet<DispatchSpecimen>();
    private Center currentCenter;
    private CollectionEvent originalCollectionEvent;
    private ProcessingEvent processingEvent;
    private SpecimenPosition specimenPosition;
    private CollectionEvent collectionEvent;
    private SpecimenType specimenType;
    private Specimen parentSpecimen;
    private Specimen topSpecimen;

    @NotEmpty
    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> comments) {
        this.commentCollection = comments;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @NotNull
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Collection<RequestSpecimen> getRequestSpecimenCollection() {
        return requestSpecimenCollection;
    }

    public void setRequestSpecimenCollection(
        Collection<RequestSpecimen> requestSpecimenCollection) {
        this.requestSpecimenCollection = requestSpecimenCollection;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Collection<Specimen> getChildSpecimenCollection() {
        return childSpecimenCollection;
    }

    public void setChildSpecimenCollection(
        Collection<Specimen> childSpecimenCollection) {
        this.childSpecimenCollection = childSpecimenCollection;
    }

    public OriginInfo getOriginInfo() {
        return originInfo;
    }

    public void setOriginInfo(OriginInfo originInfo) {
        this.originInfo = originInfo;
    }

    public Collection<DispatchSpecimen> getDispatchSpecimenCollection() {
        return dispatchSpecimenCollection;
    }

    public void setDispatchSpecimenCollection(
        Collection<DispatchSpecimen> dispatchSpecimenCollection) {
        this.dispatchSpecimenCollection = dispatchSpecimenCollection;
    }

    public Center getCurrentCenter() {
        return currentCenter;
    }

    public void setCurrentCenter(Center currentCenter) {
        this.currentCenter = currentCenter;
    }

    public CollectionEvent getOriginalCollectionEvent() {
        return originalCollectionEvent;
    }

    public void setOriginalCollectionEvent(
        CollectionEvent originalCollectionEvent) {
        this.originalCollectionEvent = originalCollectionEvent;
    }

    public ProcessingEvent getProcessingEvent() {
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEvent processingEvent) {
        this.processingEvent = processingEvent;
    }

    public SpecimenPosition getSpecimenPosition() {
        return specimenPosition;
    }

    public void setSpecimenPosition(SpecimenPosition specimenPosition) {
        this.specimenPosition = specimenPosition;
    }

    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }

    public SpecimenType getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    public Specimen getParentSpecimen() {
        return parentSpecimen;
    }

    public void setParentSpecimen(Specimen parentSpecimen) {
        this.parentSpecimen = parentSpecimen;
    }

    public Specimen getTopSpecimen() {
        return topSpecimen;
    }

    public void setTopSpecimen(Specimen topSpecimen) {
        this.topSpecimen = topSpecimen;
    }
}
