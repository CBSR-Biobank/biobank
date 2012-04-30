/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.DispatchSpecimenPeer;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchBaseWrapper;
import java.util.Arrays;

public class DispatchSpecimenBaseWrapper extends ModelWrapper<DispatchSpecimen> {

    public DispatchSpecimenBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchSpecimenBaseWrapper(WritableApplicationService appService,
        DispatchSpecimen wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<DispatchSpecimen> getWrappedClass() {
        return DispatchSpecimen.class;
    }

    @Override
    public Property<Integer, ? super DispatchSpecimen> getIdProperty() {
        return DispatchSpecimenPeer.ID;
    }

    @Override
    protected List<Property<?, ? super DispatchSpecimen>> getProperties() {
        return DispatchSpecimenPeer.PROPERTIES;
    }

    public Integer getState() {
        return getProperty(DispatchSpecimenPeer.STATE);
    }

    public void setState(Integer state) {
        setProperty(DispatchSpecimenPeer.STATE, state);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(DispatchSpecimenPeer.COMMENTS, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(DispatchSpecimenPeer.COMMENTS, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(DispatchSpecimenPeer.COMMENTS)) {
            addToWrapperCollection(DispatchSpecimenPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().add(DispatchSpecimenPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(DispatchSpecimenPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(DispatchSpecimenPeer.COMMENTS)) {
            removeFromWrapperCollection(DispatchSpecimenPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().remove(DispatchSpecimenPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchSpecimenPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchSpecimenPeer.COMMENTS, commentCollection);
    }

    public SpecimenWrapper getSpecimen() {
        boolean notCached = !isPropertyCached(DispatchSpecimenPeer.SPECIMEN);
        SpecimenWrapper specimen = getWrappedProperty(DispatchSpecimenPeer.SPECIMEN, SpecimenWrapper.class);
        if (specimen != null && notCached) ((SpecimenBaseWrapper) specimen).addToDispatchSpecimenCollectionInternal(Arrays.asList(this));
        return specimen;
    }

    public void setSpecimen(SpecimenBaseWrapper specimen) {
        if (isInitialized(DispatchSpecimenPeer.SPECIMEN)) {
            SpecimenBaseWrapper oldSpecimen = getSpecimen();
            if (oldSpecimen != null) oldSpecimen.removeFromDispatchSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (specimen != null) specimen.addToDispatchSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(DispatchSpecimenPeer.SPECIMEN, specimen);
    }

    void setSpecimenInternal(SpecimenBaseWrapper specimen) {
        setWrappedProperty(DispatchSpecimenPeer.SPECIMEN, specimen);
    }

    public DispatchWrapper getDispatch() {
        boolean notCached = !isPropertyCached(DispatchSpecimenPeer.DISPATCH);
        DispatchWrapper dispatch = getWrappedProperty(DispatchSpecimenPeer.DISPATCH, DispatchWrapper.class);
        if (dispatch != null && notCached) ((DispatchBaseWrapper) dispatch).addToDispatchSpecimenCollectionInternal(Arrays.asList(this));
        return dispatch;
    }

    public void setDispatch(DispatchBaseWrapper dispatch) {
        if (isInitialized(DispatchSpecimenPeer.DISPATCH)) {
            DispatchBaseWrapper oldDispatch = getDispatch();
            if (oldDispatch != null) oldDispatch.removeFromDispatchSpecimenCollectionInternal(Arrays.asList(this));
        }
        if (dispatch != null) dispatch.addToDispatchSpecimenCollectionInternal(Arrays.asList(this));
        setWrappedProperty(DispatchSpecimenPeer.DISPATCH, dispatch);
    }

    void setDispatchInternal(DispatchBaseWrapper dispatch) {
        setWrappedProperty(DispatchSpecimenPeer.DISPATCH, dispatch);
    }

}
