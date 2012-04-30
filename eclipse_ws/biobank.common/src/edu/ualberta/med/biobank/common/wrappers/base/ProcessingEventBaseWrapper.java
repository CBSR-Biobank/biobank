/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import java.util.Date;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CenterBaseWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import java.util.Arrays;

public class ProcessingEventBaseWrapper extends ModelWrapper<ProcessingEvent> {

    public ProcessingEventBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ProcessingEventBaseWrapper(WritableApplicationService appService,
        ProcessingEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<ProcessingEvent> getWrappedClass() {
        return ProcessingEvent.class;
    }

    @Override
    public Property<Integer, ? super ProcessingEvent> getIdProperty() {
        return ProcessingEventPeer.ID;
    }

    @Override
    protected List<Property<?, ? super ProcessingEvent>> getProperties() {
        return ProcessingEventPeer.PROPERTIES;
    }

    public Date getCreatedAt() {
        return getProperty(ProcessingEventPeer.CREATED_AT);
    }

    public void setCreatedAt(Date createdAt) {
        setProperty(ProcessingEventPeer.CREATED_AT, createdAt);
    }

    public String getWorksheet() {
        return getProperty(ProcessingEventPeer.WORKSHEET);
    }

    public void setWorksheet(String worksheet) {
        String trimmed = worksheet == null ? null : worksheet.trim();
        setProperty(ProcessingEventPeer.WORKSHEET, trimmed);
    }

   @SuppressWarnings("unchecked")
    public CenterWrapper<?> getCenter() {
        boolean notCached = !isPropertyCached(ProcessingEventPeer.CENTER);
        CenterWrapper <?>center = getWrappedProperty(ProcessingEventPeer.CENTER, CenterWrapper.class);
        if (center != null && notCached) ((CenterBaseWrapper<?>) center).addToProcessingEventCollectionInternal(Arrays.asList(this));
        return center;
    }

    public void setCenter(CenterBaseWrapper<?> center) {
        if (isInitialized(ProcessingEventPeer.CENTER)) {
            CenterBaseWrapper<?> oldCenter = getCenter();
            if (oldCenter != null) oldCenter.removeFromProcessingEventCollectionInternal(Arrays.asList(this));
        }
        if (center != null) center.addToProcessingEventCollectionInternal(Arrays.asList(this));
        setWrappedProperty(ProcessingEventPeer.CENTER, center);
    }

    void setCenterInternal(CenterBaseWrapper<?> center) {
        setWrappedProperty(ProcessingEventPeer.CENTER, center);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(ProcessingEventPeer.COMMENTS, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(ProcessingEventPeer.COMMENTS, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(ProcessingEventPeer.COMMENTS)) {
            addToWrapperCollection(ProcessingEventPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().add(ProcessingEventPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(ProcessingEventPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(ProcessingEventPeer.COMMENTS)) {
            removeFromWrapperCollection(ProcessingEventPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().remove(ProcessingEventPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ProcessingEventPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ProcessingEventPeer.COMMENTS, commentCollection);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }

    public List<SpecimenWrapper> getSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(ProcessingEventPeer.SPECIMENS);
        List<SpecimenWrapper> specimenCollection = getWrapperCollection(ProcessingEventPeer.SPECIMENS, SpecimenWrapper.class, sort);
        if (notCached) {
            for (SpecimenBaseWrapper e : specimenCollection) {
                e.setProcessingEventInternal(this);
            }
        }
        return specimenCollection;
    }

    public void addToSpecimenCollection(List<? extends SpecimenBaseWrapper> specimenCollection) {
        addToWrapperCollection(ProcessingEventPeer.SPECIMENS, specimenCollection);
        for (SpecimenBaseWrapper e : specimenCollection) {
            e.setProcessingEventInternal(this);
        }
    }

    void addToSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> specimenCollection) {
        if (isInitialized(ProcessingEventPeer.SPECIMENS)) {
            addToWrapperCollection(ProcessingEventPeer.SPECIMENS, specimenCollection);
        } else {
            getElementQueue().add(ProcessingEventPeer.SPECIMENS, specimenCollection);
        }
    }

    public void removeFromSpecimenCollection(List<? extends SpecimenBaseWrapper> specimenCollection) {
        removeFromWrapperCollection(ProcessingEventPeer.SPECIMENS, specimenCollection);
        for (SpecimenBaseWrapper e : specimenCollection) {
            e.setProcessingEventInternal(null);
        }
    }

    void removeFromSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> specimenCollection) {
        if (isPropertyCached(ProcessingEventPeer.SPECIMENS)) {
            removeFromWrapperCollection(ProcessingEventPeer.SPECIMENS, specimenCollection);
        } else {
            getElementQueue().remove(ProcessingEventPeer.SPECIMENS, specimenCollection);
        }
    }

    public void removeFromSpecimenCollectionWithCheck(List<? extends SpecimenBaseWrapper> specimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ProcessingEventPeer.SPECIMENS, specimenCollection);
        for (SpecimenBaseWrapper e : specimenCollection) {
            e.setProcessingEventInternal(null);
        }
    }

    void removeFromSpecimenCollectionWithCheckInternal(List<? extends SpecimenBaseWrapper> specimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(ProcessingEventPeer.SPECIMENS, specimenCollection);
    }

}
