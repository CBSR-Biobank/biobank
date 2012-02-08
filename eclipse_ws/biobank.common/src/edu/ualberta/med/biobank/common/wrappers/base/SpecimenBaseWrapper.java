/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import java.util.Date;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ProcessingEventBaseWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CollectionEventBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchSpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CenterBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.SpecimenPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenPositionBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.RequestSpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenTypeBaseWrapper;
import java.util.Arrays;

public class SpecimenBaseWrapper extends ModelWrapper<Specimen> {

    public SpecimenBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public SpecimenBaseWrapper(WritableApplicationService appService,
        Specimen wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Specimen> getWrappedClass() {
        return Specimen.class;
    }

    @Override
    public Property<Integer, ? super Specimen> getIdProperty() {
        return SpecimenPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Specimen>> getProperties() {
        return SpecimenPeer.PROPERTIES;
    }

    public Date getCreatedAt() {
        return getProperty(SpecimenPeer.CREATED_AT);
    }

    public void setCreatedAt(Date createdAt) {
        setProperty(SpecimenPeer.CREATED_AT, createdAt);
    }

    public String getInventoryId() {
        return getProperty(SpecimenPeer.INVENTORY_ID);
    }

    public void setInventoryId(String inventoryId) {
        String trimmed = inventoryId == null ? null : inventoryId.trim();
        setProperty(SpecimenPeer.INVENTORY_ID, trimmed);
    }

    public Double getQuantity() {
        return getProperty(SpecimenPeer.QUANTITY);
    }

    public void setQuantity(Double quantity) {
        setProperty(SpecimenPeer.QUANTITY, quantity);
    }

    public ProcessingEventWrapper getProcessingEvent() {
        boolean notCached = !isPropertyCached(SpecimenPeer.PROCESSING_EVENT);
        ProcessingEventWrapper processingEvent = getWrappedProperty(SpecimenPeer.PROCESSING_EVENT, ProcessingEventWrapper.class);
        if (processingEvent != null && notCached) ((ProcessingEventBaseWrapper) processingEvent).addToSpecimenCollectionInternal(Arrays.asList(this));
        return processingEvent;
    }

    public void setProcessingEvent(ProcessingEventBaseWrapper processingEvent) {
        if (isInitialized(SpecimenPeer.PROCESSING_EVENT)) {
            ProcessingEventBaseWrapper oldProcessingEvent = getProcessingEvent();
            if (oldProcessingEvent != null) oldProcessingEvent.removeFromSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (processingEvent != null) processingEvent.addToSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(SpecimenPeer.PROCESSING_EVENT, processingEvent);
    }

    void setProcessingEventInternal(ProcessingEventBaseWrapper processingEvent) {
        setWrappedProperty(SpecimenPeer.PROCESSING_EVENT, processingEvent);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(SpecimenPeer.COMMENT_COLLECTION, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(SpecimenPeer.COMMENT_COLLECTION, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(SpecimenPeer.COMMENT_COLLECTION)) {
            addToWrapperCollection(SpecimenPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().add(SpecimenPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(SpecimenPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(SpecimenPeer.COMMENT_COLLECTION)) {
            removeFromWrapperCollection(SpecimenPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().remove(SpecimenPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenPeer.COMMENT_COLLECTION, commentCollection);
    }

    public OriginInfoWrapper getOriginInfo() {
        boolean notCached = !isPropertyCached(SpecimenPeer.ORIGIN_INFO);
        OriginInfoWrapper originInfo = getWrappedProperty(SpecimenPeer.ORIGIN_INFO, OriginInfoWrapper.class);
        if (originInfo != null && notCached) ((OriginInfoBaseWrapper) originInfo).addToSpecimenCollectionInternal(Arrays.asList(this));
        return originInfo;
    }

    public void setOriginInfo(OriginInfoBaseWrapper originInfo) {
        if (isInitialized(SpecimenPeer.ORIGIN_INFO)) {
            OriginInfoBaseWrapper oldOriginInfo = getOriginInfo();
            if (oldOriginInfo != null) oldOriginInfo.removeFromSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (originInfo != null) originInfo.addToSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(SpecimenPeer.ORIGIN_INFO, originInfo);
    }

    void setOriginInfoInternal(OriginInfoBaseWrapper originInfo) {
        setWrappedProperty(SpecimenPeer.ORIGIN_INFO, originInfo);
    }

    public SpecimenWrapper getTopSpecimen() {
        SpecimenWrapper topSpecimen = getWrappedProperty(SpecimenPeer.TOP_SPECIMEN, SpecimenWrapper.class);
        return topSpecimen;
    }

    public void setTopSpecimen(SpecimenBaseWrapper topSpecimen) {
        setWrappedProperty(SpecimenPeer.TOP_SPECIMEN, topSpecimen);
    }

    void setTopSpecimenInternal(SpecimenBaseWrapper topSpecimen) {
        setWrappedProperty(SpecimenPeer.TOP_SPECIMEN, topSpecimen);
    }

    public CollectionEventWrapper getCollectionEvent() {
        boolean notCached = !isPropertyCached(SpecimenPeer.COLLECTION_EVENT);
        CollectionEventWrapper collectionEvent = getWrappedProperty(SpecimenPeer.COLLECTION_EVENT, CollectionEventWrapper.class);
        if (collectionEvent != null && notCached) ((CollectionEventBaseWrapper) collectionEvent).addToAllSpecimenCollectionInternal(Arrays.asList(this));
        return collectionEvent;
    }

    public void setCollectionEvent(CollectionEventBaseWrapper collectionEvent) {
        if (isInitialized(SpecimenPeer.COLLECTION_EVENT)) {
            CollectionEventBaseWrapper oldCollectionEvent = getCollectionEvent();
            if (oldCollectionEvent != null) oldCollectionEvent.removeFromAllSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (collectionEvent != null) collectionEvent.addToAllSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(SpecimenPeer.COLLECTION_EVENT, collectionEvent);
    }

    void setCollectionEventInternal(CollectionEventBaseWrapper collectionEvent) {
        setWrappedProperty(SpecimenPeer.COLLECTION_EVENT, collectionEvent);
    }

    public CollectionEventWrapper getOriginalCollectionEvent() {
        boolean notCached = !isPropertyCached(SpecimenPeer.ORIGINAL_COLLECTION_EVENT);
        CollectionEventWrapper originalCollectionEvent = getWrappedProperty(SpecimenPeer.ORIGINAL_COLLECTION_EVENT, CollectionEventWrapper.class);
        if (originalCollectionEvent != null && notCached) ((CollectionEventBaseWrapper) originalCollectionEvent).addToOriginalSpecimenCollectionInternal(Arrays.asList(this));
        return originalCollectionEvent;
    }

    public void setOriginalCollectionEvent(CollectionEventBaseWrapper originalCollectionEvent) {
        if (isInitialized(SpecimenPeer.ORIGINAL_COLLECTION_EVENT)) {
            CollectionEventBaseWrapper oldOriginalCollectionEvent = getOriginalCollectionEvent();
            if (oldOriginalCollectionEvent != null) oldOriginalCollectionEvent.removeFromOriginalSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (originalCollectionEvent != null) originalCollectionEvent.addToOriginalSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(SpecimenPeer.ORIGINAL_COLLECTION_EVENT, originalCollectionEvent);
    }

    void setOriginalCollectionEventInternal(CollectionEventBaseWrapper originalCollectionEvent) {
        setWrappedProperty(SpecimenPeer.ORIGINAL_COLLECTION_EVENT, originalCollectionEvent);
    }

    public SpecimenWrapper getParentSpecimen() {
        boolean notCached = !isPropertyCached(SpecimenPeer.PARENT_SPECIMEN);
        SpecimenWrapper parentSpecimen = getWrappedProperty(SpecimenPeer.PARENT_SPECIMEN, SpecimenWrapper.class);
        if (parentSpecimen != null && notCached) ((SpecimenBaseWrapper) parentSpecimen).addToChildSpecimenCollectionInternal(Arrays.asList(this));
        return parentSpecimen;
    }

    public void setParentSpecimen(SpecimenBaseWrapper parentSpecimen) {
        if (isInitialized(SpecimenPeer.PARENT_SPECIMEN)) {
            SpecimenBaseWrapper oldParentSpecimen = getParentSpecimen();
            if (oldParentSpecimen != null) oldParentSpecimen.removeFromChildSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (parentSpecimen != null) parentSpecimen.addToChildSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(SpecimenPeer.PARENT_SPECIMEN, parentSpecimen);
    }

    void setParentSpecimenInternal(SpecimenBaseWrapper parentSpecimen) {
        setWrappedProperty(SpecimenPeer.PARENT_SPECIMEN, parentSpecimen);
    }

    public List<SpecimenWrapper> getChildSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(SpecimenPeer.CHILD_SPECIMEN_COLLECTION);
        List<SpecimenWrapper> childSpecimenCollection = getWrapperCollection(SpecimenPeer.CHILD_SPECIMEN_COLLECTION, SpecimenWrapper.class, sort);
        if (notCached) {
            for (SpecimenBaseWrapper e : childSpecimenCollection) {
                e.setParentSpecimenInternal(this);
            }
        }
        return childSpecimenCollection;
    }

    public void addToChildSpecimenCollection(List<? extends SpecimenBaseWrapper> childSpecimenCollection) {
        addToWrapperCollection(SpecimenPeer.CHILD_SPECIMEN_COLLECTION, childSpecimenCollection);
        for (SpecimenBaseWrapper e : childSpecimenCollection) {
            e.setParentSpecimenInternal(this);
        }
    }

    void addToChildSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> childSpecimenCollection) {
        if (isInitialized(SpecimenPeer.CHILD_SPECIMEN_COLLECTION)) {
            addToWrapperCollection(SpecimenPeer.CHILD_SPECIMEN_COLLECTION, childSpecimenCollection);
        } else {
            getElementQueue().add(SpecimenPeer.CHILD_SPECIMEN_COLLECTION, childSpecimenCollection);
        }
    }

    public void removeFromChildSpecimenCollection(List<? extends SpecimenBaseWrapper> childSpecimenCollection) {
        removeFromWrapperCollection(SpecimenPeer.CHILD_SPECIMEN_COLLECTION, childSpecimenCollection);
        for (SpecimenBaseWrapper e : childSpecimenCollection) {
            e.setParentSpecimenInternal(null);
        }
    }

    void removeFromChildSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> childSpecimenCollection) {
        if (isPropertyCached(SpecimenPeer.CHILD_SPECIMEN_COLLECTION)) {
            removeFromWrapperCollection(SpecimenPeer.CHILD_SPECIMEN_COLLECTION, childSpecimenCollection);
        } else {
            getElementQueue().remove(SpecimenPeer.CHILD_SPECIMEN_COLLECTION, childSpecimenCollection);
        }
    }

    public void removeFromChildSpecimenCollectionWithCheck(List<? extends SpecimenBaseWrapper> childSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenPeer.CHILD_SPECIMEN_COLLECTION, childSpecimenCollection);
        for (SpecimenBaseWrapper e : childSpecimenCollection) {
            e.setParentSpecimenInternal(null);
        }
    }

    void removeFromChildSpecimenCollectionWithCheckInternal(List<? extends SpecimenBaseWrapper> childSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenPeer.CHILD_SPECIMEN_COLLECTION, childSpecimenCollection);
    }

    public List<DispatchSpecimenWrapper> getDispatchSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION);
        List<DispatchSpecimenWrapper> dispatchSpecimenCollection = getWrapperCollection(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION, DispatchSpecimenWrapper.class, sort);
        if (notCached) {
            for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
                e.setSpecimenInternal(this);
            }
        }
        return dispatchSpecimenCollection;
    }

    public void addToDispatchSpecimenCollection(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        addToWrapperCollection(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
            e.setSpecimenInternal(this);
        }
    }

    void addToDispatchSpecimenCollectionInternal(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        if (isInitialized(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION)) {
            addToWrapperCollection(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        } else {
            getElementQueue().add(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        }
    }

    public void removeFromDispatchSpecimenCollection(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        removeFromWrapperCollection(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
            e.setSpecimenInternal(null);
        }
    }

    void removeFromDispatchSpecimenCollectionInternal(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        if (isPropertyCached(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION)) {
            removeFromWrapperCollection(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        } else {
            getElementQueue().remove(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        }
    }

    public void removeFromDispatchSpecimenCollectionWithCheck(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
            e.setSpecimenInternal(null);
        }
    }

    void removeFromDispatchSpecimenCollectionWithCheckInternal(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
    }

   @SuppressWarnings("unchecked")
    public CenterWrapper<?> getCurrentCenter() {
        CenterWrapper <?>currentCenter = getWrappedProperty(SpecimenPeer.CURRENT_CENTER, CenterWrapper.class);
        return currentCenter;
    }

    public void setCurrentCenter(CenterBaseWrapper<?> currentCenter) {
        setWrappedProperty(SpecimenPeer.CURRENT_CENTER, currentCenter);
    }

    void setCurrentCenterInternal(CenterBaseWrapper<?> currentCenter) {
        setWrappedProperty(SpecimenPeer.CURRENT_CENTER, currentCenter);
    }

    public SpecimenPositionWrapper getSpecimenPosition() {
        boolean notCached = !isPropertyCached(SpecimenPeer.SPECIMEN_POSITION);
        SpecimenPositionWrapper specimenPosition = getWrappedProperty(SpecimenPeer.SPECIMEN_POSITION, SpecimenPositionWrapper.class);
        if (specimenPosition != null && notCached) ((SpecimenPositionBaseWrapper) specimenPosition).setSpecimenInternal(this);
        return specimenPosition;
    }

    public void setSpecimenPosition(SpecimenPositionBaseWrapper specimenPosition) {
        if (isInitialized(SpecimenPeer.SPECIMEN_POSITION)) {
            SpecimenPositionBaseWrapper oldSpecimenPosition = getSpecimenPosition();
            if (oldSpecimenPosition != null) oldSpecimenPosition.setSpecimenInternal(null);
        }
        if (specimenPosition != null) specimenPosition.setSpecimenInternal(this);
        setWrappedProperty(SpecimenPeer.SPECIMEN_POSITION, specimenPosition);
    }

    void setSpecimenPositionInternal(SpecimenPositionBaseWrapper specimenPosition) {
        setWrappedProperty(SpecimenPeer.SPECIMEN_POSITION, specimenPosition);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }

    public List<RequestSpecimenWrapper> getRequestSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION);
        List<RequestSpecimenWrapper> requestSpecimenCollection = getWrapperCollection(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION, RequestSpecimenWrapper.class, sort);
        if (notCached) {
            for (RequestSpecimenBaseWrapper e : requestSpecimenCollection) {
                e.setSpecimenInternal(this);
            }
        }
        return requestSpecimenCollection;
    }

    public void addToRequestSpecimenCollection(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) {
        addToWrapperCollection(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION, requestSpecimenCollection);
        for (RequestSpecimenBaseWrapper e : requestSpecimenCollection) {
            e.setSpecimenInternal(this);
        }
    }

    void addToRequestSpecimenCollectionInternal(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) {
        if (isInitialized(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION)) {
            addToWrapperCollection(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION, requestSpecimenCollection);
        } else {
            getElementQueue().add(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION, requestSpecimenCollection);
        }
    }

    public void removeFromRequestSpecimenCollection(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) {
        removeFromWrapperCollection(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION, requestSpecimenCollection);
        for (RequestSpecimenBaseWrapper e : requestSpecimenCollection) {
            e.setSpecimenInternal(null);
        }
    }

    void removeFromRequestSpecimenCollectionInternal(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) {
        if (isPropertyCached(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION)) {
            removeFromWrapperCollection(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION, requestSpecimenCollection);
        } else {
            getElementQueue().remove(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION, requestSpecimenCollection);
        }
    }

    public void removeFromRequestSpecimenCollectionWithCheck(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION, requestSpecimenCollection);
        for (RequestSpecimenBaseWrapper e : requestSpecimenCollection) {
            e.setSpecimenInternal(null);
        }
    }

    void removeFromRequestSpecimenCollectionWithCheckInternal(List<? extends RequestSpecimenBaseWrapper> requestSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(SpecimenPeer.REQUEST_SPECIMEN_COLLECTION, requestSpecimenCollection);
    }

    public SpecimenTypeWrapper getSpecimenType() {
        SpecimenTypeWrapper specimenType = getWrappedProperty(SpecimenPeer.SPECIMEN_TYPE, SpecimenTypeWrapper.class);
        return specimenType;
    }

    public void setSpecimenType(SpecimenTypeBaseWrapper specimenType) {
        setWrappedProperty(SpecimenPeer.SPECIMEN_TYPE, specimenType);
    }

    void setSpecimenTypeInternal(SpecimenTypeBaseWrapper specimenType) {
        setWrappedProperty(SpecimenPeer.SPECIMEN_TYPE, specimenType);
    }

}
