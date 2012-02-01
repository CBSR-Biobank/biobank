/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import java.util.Date;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CollectionEventBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.StudyBaseWrapper;
import java.util.Arrays;

public class PatientBaseWrapper extends ModelWrapper<Patient> {

    public PatientBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public PatientBaseWrapper(WritableApplicationService appService,
        Patient wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Patient> getWrappedClass() {
        return Patient.class;
    }

    @Override
    public Property<Integer, ? super Patient> getIdProperty() {
        return PatientPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Patient>> getProperties() {
        return PatientPeer.PROPERTIES;
    }

    public Date getCreatedAt() {
        return getProperty(PatientPeer.CREATED_AT);
    }

    public void setCreatedAt(Date createdAt) {
        setProperty(PatientPeer.CREATED_AT, createdAt);
    }

    public String getPnumber() {
        return getProperty(PatientPeer.PNUMBER);
    }

    public void setPnumber(String pnumber) {
        String trimmed = pnumber == null ? null : pnumber.trim();
        setProperty(PatientPeer.PNUMBER, trimmed);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(PatientPeer.COMMENT_COLLECTION, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(PatientPeer.COMMENT_COLLECTION, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(PatientPeer.COMMENT_COLLECTION)) {
            addToWrapperCollection(PatientPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().add(PatientPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(PatientPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(PatientPeer.COMMENT_COLLECTION)) {
            removeFromWrapperCollection(PatientPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().remove(PatientPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(PatientPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(PatientPeer.COMMENT_COLLECTION, commentCollection);
    }

    public List<CollectionEventWrapper> getCollectionEventCollection(boolean sort) {
        boolean notCached = !isPropertyCached(PatientPeer.COLLECTION_EVENT_COLLECTION);
        List<CollectionEventWrapper> collectionEventCollection = getWrapperCollection(PatientPeer.COLLECTION_EVENT_COLLECTION, CollectionEventWrapper.class, sort);
        if (notCached) {
            for (CollectionEventBaseWrapper e : collectionEventCollection) {
                e.setPatientInternal(this);
            }
        }
        return collectionEventCollection;
    }

    public void addToCollectionEventCollection(List<? extends CollectionEventBaseWrapper> collectionEventCollection) {
        addToWrapperCollection(PatientPeer.COLLECTION_EVENT_COLLECTION, collectionEventCollection);
        for (CollectionEventBaseWrapper e : collectionEventCollection) {
            e.setPatientInternal(this);
        }
    }

    void addToCollectionEventCollectionInternal(List<? extends CollectionEventBaseWrapper> collectionEventCollection) {
        if (isInitialized(PatientPeer.COLLECTION_EVENT_COLLECTION)) {
            addToWrapperCollection(PatientPeer.COLLECTION_EVENT_COLLECTION, collectionEventCollection);
        } else {
            getElementQueue().add(PatientPeer.COLLECTION_EVENT_COLLECTION, collectionEventCollection);
        }
    }

    public void removeFromCollectionEventCollection(List<? extends CollectionEventBaseWrapper> collectionEventCollection) {
        removeFromWrapperCollection(PatientPeer.COLLECTION_EVENT_COLLECTION, collectionEventCollection);
        for (CollectionEventBaseWrapper e : collectionEventCollection) {
            e.setPatientInternal(null);
        }
    }

    void removeFromCollectionEventCollectionInternal(List<? extends CollectionEventBaseWrapper> collectionEventCollection) {
        if (isPropertyCached(PatientPeer.COLLECTION_EVENT_COLLECTION)) {
            removeFromWrapperCollection(PatientPeer.COLLECTION_EVENT_COLLECTION, collectionEventCollection);
        } else {
            getElementQueue().remove(PatientPeer.COLLECTION_EVENT_COLLECTION, collectionEventCollection);
        }
    }

    public void removeFromCollectionEventCollectionWithCheck(List<? extends CollectionEventBaseWrapper> collectionEventCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(PatientPeer.COLLECTION_EVENT_COLLECTION, collectionEventCollection);
        for (CollectionEventBaseWrapper e : collectionEventCollection) {
            e.setPatientInternal(null);
        }
    }

    void removeFromCollectionEventCollectionWithCheckInternal(List<? extends CollectionEventBaseWrapper> collectionEventCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(PatientPeer.COLLECTION_EVENT_COLLECTION, collectionEventCollection);
    }

    public StudyWrapper getStudy() {
        boolean notCached = !isPropertyCached(PatientPeer.STUDY);
        StudyWrapper study = getWrappedProperty(PatientPeer.STUDY, StudyWrapper.class);
        if (study != null && notCached) ((StudyBaseWrapper) study).addToPatientCollectionInternal(Arrays.asList(this));
        return study;
    }

    public void setStudy(StudyBaseWrapper study) {
        if (isInitialized(PatientPeer.STUDY)) {
            StudyBaseWrapper oldStudy = getStudy();
            if (oldStudy != null) oldStudy.removeFromPatientCollectionInternal(Arrays.asList(this));
        }
        if (study != null) study.addToPatientCollectionInternal(Arrays.asList(this));
        setWrappedProperty(PatientPeer.STUDY, study);
    }

    void setStudyInternal(StudyBaseWrapper study) {
        setWrappedProperty(PatientPeer.STUDY, study);
    }

}
