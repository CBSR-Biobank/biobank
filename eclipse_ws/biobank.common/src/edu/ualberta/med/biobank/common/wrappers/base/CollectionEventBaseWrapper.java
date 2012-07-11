/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.CollectionEvent;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CollectionEventBaseWrapper extends ModelWrapper<CollectionEvent> {

    public CollectionEventBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public CollectionEventBaseWrapper(WritableApplicationService appService,
        CollectionEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<CollectionEvent> getWrappedClass() {
        return CollectionEvent.class;
    }

    @Override
    public Property<Integer, ? super CollectionEvent> getIdProperty() {
        return CollectionEventPeer.ID;
    }

    @Override
    protected List<Property<?, ? super CollectionEvent>> getProperties() {
        return CollectionEventPeer.PROPERTIES;
    }

    public Integer getVisitNumber() {
        return getProperty(CollectionEventPeer.VISIT_NUMBER);
    }

    public void setVisitNumber(Integer visitNumber) {
        setProperty(CollectionEventPeer.VISIT_NUMBER, visitNumber);
    }

    public List<SpecimenWrapper> getAllSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CollectionEventPeer.ALL_SPECIMENS);
        List<SpecimenWrapper> allSpecimenCollection = getWrapperCollection(CollectionEventPeer.ALL_SPECIMENS, SpecimenWrapper.class, sort);
        if (notCached) {
            for (SpecimenBaseWrapper e : allSpecimenCollection) {
                e.setCollectionEventInternal(this);
            }
        }
        return allSpecimenCollection;
    }

    public void addToAllSpecimenCollection(List<? extends SpecimenBaseWrapper> allSpecimenCollection) {
        addToWrapperCollection(CollectionEventPeer.ALL_SPECIMENS, allSpecimenCollection);
        for (SpecimenBaseWrapper e : allSpecimenCollection) {
            e.setCollectionEventInternal(this);
        }
    }

    void addToAllSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> allSpecimenCollection) {
        if (isInitialized(CollectionEventPeer.ALL_SPECIMENS)) {
            addToWrapperCollection(CollectionEventPeer.ALL_SPECIMENS, allSpecimenCollection);
        } else {
            getElementQueue().add(CollectionEventPeer.ALL_SPECIMENS, allSpecimenCollection);
        }
    }

    public void removeFromAllSpecimenCollection(List<? extends SpecimenBaseWrapper> allSpecimenCollection) {
        removeFromWrapperCollection(CollectionEventPeer.ALL_SPECIMENS, allSpecimenCollection);
        for (SpecimenBaseWrapper e : allSpecimenCollection) {
            e.setCollectionEventInternal(null);
        }
    }

    void removeFromAllSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> allSpecimenCollection) {
        if (isPropertyCached(CollectionEventPeer.ALL_SPECIMENS)) {
            removeFromWrapperCollection(CollectionEventPeer.ALL_SPECIMENS, allSpecimenCollection);
        } else {
            getElementQueue().remove(CollectionEventPeer.ALL_SPECIMENS, allSpecimenCollection);
        }
    }

    public void removeFromAllSpecimenCollectionWithCheck(List<? extends SpecimenBaseWrapper> allSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CollectionEventPeer.ALL_SPECIMENS, allSpecimenCollection);
        for (SpecimenBaseWrapper e : allSpecimenCollection) {
            e.setCollectionEventInternal(null);
        }
    }

    void removeFromAllSpecimenCollectionWithCheckInternal(List<? extends SpecimenBaseWrapper> allSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CollectionEventPeer.ALL_SPECIMENS, allSpecimenCollection);
    }

    public PatientWrapper getPatient() {
        boolean notCached = !isPropertyCached(CollectionEventPeer.PATIENT);
        PatientWrapper patient = getWrappedProperty(CollectionEventPeer.PATIENT, PatientWrapper.class);
        if (patient != null && notCached) ((PatientBaseWrapper) patient).addToCollectionEventCollectionInternal(Arrays.asList(this));
        return patient;
    }

    public void setPatient(PatientBaseWrapper patient) {
        if (isInitialized(CollectionEventPeer.PATIENT)) {
            PatientBaseWrapper oldPatient = getPatient();
            if (oldPatient != null) oldPatient.removeFromCollectionEventCollectionInternal(Arrays.asList(this));
        }
        if (patient != null) patient.addToCollectionEventCollectionInternal(Arrays.asList(this));
        setWrappedProperty(CollectionEventPeer.PATIENT, patient);
    }

    void setPatientInternal(PatientBaseWrapper patient) {
        setWrappedProperty(CollectionEventPeer.PATIENT, patient);
    }

    public List<EventAttrWrapper> getEventAttrCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CollectionEventPeer.EVENT_ATTRS);
        List<EventAttrWrapper> eventAttrCollection = getWrapperCollection(CollectionEventPeer.EVENT_ATTRS, EventAttrWrapper.class, sort);
        if (notCached) {
            for (EventAttrBaseWrapper e : eventAttrCollection) {
                e.setCollectionEventInternal(this);
            }
        }
        return eventAttrCollection;
    }

    public void addToEventAttrCollection(List<? extends EventAttrBaseWrapper> eventAttrCollection) {
        addToWrapperCollection(CollectionEventPeer.EVENT_ATTRS, eventAttrCollection);
        for (EventAttrBaseWrapper e : eventAttrCollection) {
            e.setCollectionEventInternal(this);
        }
    }

    void addToEventAttrCollectionInternal(List<? extends EventAttrBaseWrapper> eventAttrCollection) {
        if (isInitialized(CollectionEventPeer.EVENT_ATTRS)) {
            addToWrapperCollection(CollectionEventPeer.EVENT_ATTRS, eventAttrCollection);
        } else {
            getElementQueue().add(CollectionEventPeer.EVENT_ATTRS, eventAttrCollection);
        }
    }

    public void removeFromEventAttrCollection(List<? extends EventAttrBaseWrapper> eventAttrCollection) {
        removeFromWrapperCollection(CollectionEventPeer.EVENT_ATTRS, eventAttrCollection);
        for (EventAttrBaseWrapper e : eventAttrCollection) {
            e.setCollectionEventInternal(null);
        }
    }

    void removeFromEventAttrCollectionInternal(List<? extends EventAttrBaseWrapper> eventAttrCollection) {
        if (isPropertyCached(CollectionEventPeer.EVENT_ATTRS)) {
            removeFromWrapperCollection(CollectionEventPeer.EVENT_ATTRS, eventAttrCollection);
        } else {
            getElementQueue().remove(CollectionEventPeer.EVENT_ATTRS, eventAttrCollection);
        }
    }

    public void removeFromEventAttrCollectionWithCheck(List<? extends EventAttrBaseWrapper> eventAttrCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CollectionEventPeer.EVENT_ATTRS, eventAttrCollection);
        for (EventAttrBaseWrapper e : eventAttrCollection) {
            e.setCollectionEventInternal(null);
        }
    }

    void removeFromEventAttrCollectionWithCheckInternal(List<? extends EventAttrBaseWrapper> eventAttrCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CollectionEventPeer.EVENT_ATTRS, eventAttrCollection);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(CollectionEventPeer.COMMENTS, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(CollectionEventPeer.COMMENTS, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(CollectionEventPeer.COMMENTS)) {
            addToWrapperCollection(CollectionEventPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().add(CollectionEventPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(CollectionEventPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(CollectionEventPeer.COMMENTS)) {
            removeFromWrapperCollection(CollectionEventPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().remove(CollectionEventPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CollectionEventPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CollectionEventPeer.COMMENTS, commentCollection);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }

    public List<SpecimenWrapper> getOriginalSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(CollectionEventPeer.ORIGINAL_SPECIMENS);
        List<SpecimenWrapper> originalSpecimenCollection = getWrapperCollection(CollectionEventPeer.ORIGINAL_SPECIMENS, SpecimenWrapper.class, sort);
        if (notCached) {
            for (SpecimenBaseWrapper e : originalSpecimenCollection) {
                e.setOriginalCollectionEventInternal(this);
            }
        }
        return originalSpecimenCollection;
    }

    public void addToOriginalSpecimenCollection(List<? extends SpecimenBaseWrapper> originalSpecimenCollection) {
        addToWrapperCollection(CollectionEventPeer.ORIGINAL_SPECIMENS, originalSpecimenCollection);
        for (SpecimenBaseWrapper e : originalSpecimenCollection) {
            e.setOriginalCollectionEventInternal(this);
        }
    }

    void addToOriginalSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> originalSpecimenCollection) {
        if (isInitialized(CollectionEventPeer.ORIGINAL_SPECIMENS)) {
            addToWrapperCollection(CollectionEventPeer.ORIGINAL_SPECIMENS, originalSpecimenCollection);
        } else {
            getElementQueue().add(CollectionEventPeer.ORIGINAL_SPECIMENS, originalSpecimenCollection);
        }
    }

    public void removeFromOriginalSpecimenCollection(List<? extends SpecimenBaseWrapper> originalSpecimenCollection) {
        removeFromWrapperCollection(CollectionEventPeer.ORIGINAL_SPECIMENS, originalSpecimenCollection);
        for (SpecimenBaseWrapper e : originalSpecimenCollection) {
            e.setOriginalCollectionEventInternal(null);
        }
    }

    void removeFromOriginalSpecimenCollectionInternal(List<? extends SpecimenBaseWrapper> originalSpecimenCollection) {
        if (isPropertyCached(CollectionEventPeer.ORIGINAL_SPECIMENS)) {
            removeFromWrapperCollection(CollectionEventPeer.ORIGINAL_SPECIMENS, originalSpecimenCollection);
        } else {
            getElementQueue().remove(CollectionEventPeer.ORIGINAL_SPECIMENS, originalSpecimenCollection);
        }
    }

    public void removeFromOriginalSpecimenCollectionWithCheck(List<? extends SpecimenBaseWrapper> originalSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CollectionEventPeer.ORIGINAL_SPECIMENS, originalSpecimenCollection);
        for (SpecimenBaseWrapper e : originalSpecimenCollection) {
            e.setOriginalCollectionEventInternal(null);
        }
    }

    void removeFromOriginalSpecimenCollectionWithCheckInternal(List<? extends SpecimenBaseWrapper> originalSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(CollectionEventPeer.ORIGINAL_SPECIMENS, originalSpecimenCollection);
    }

}
