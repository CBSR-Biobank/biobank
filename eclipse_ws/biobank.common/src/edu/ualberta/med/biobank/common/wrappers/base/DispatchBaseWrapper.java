/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ShipmentInfoBaseWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CenterBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchSpecimenBaseWrapper;
import java.util.Arrays;

public class DispatchBaseWrapper extends ModelWrapper<Dispatch> {

    public DispatchBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchBaseWrapper(WritableApplicationService appService,
        Dispatch wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Dispatch> getWrappedClass() {
        return Dispatch.class;
    }

    @Override
    public Property<Integer, ? super Dispatch> getIdProperty() {
        return DispatchPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Dispatch>> getProperties() {
        return DispatchPeer.PROPERTIES;
    }

    public Integer getState() {
        return getProperty(DispatchPeer.STATE);
    }

    public void setState(Integer state) {
        setProperty(DispatchPeer.STATE, state);
    }

    public ShipmentInfoWrapper getShipmentInfo() {
        ShipmentInfoWrapper shipmentInfo = getWrappedProperty(DispatchPeer.SHIPMENT_INFO, ShipmentInfoWrapper.class);
        return shipmentInfo;
    }

    public void setShipmentInfo(ShipmentInfoBaseWrapper shipmentInfo) {
        setWrappedProperty(DispatchPeer.SHIPMENT_INFO, shipmentInfo);
    }

    void setShipmentInfoInternal(ShipmentInfoBaseWrapper shipmentInfo) {
        setWrappedProperty(DispatchPeer.SHIPMENT_INFO, shipmentInfo);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(DispatchPeer.COMMENT_COLLECTION, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(DispatchPeer.COMMENT_COLLECTION, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(DispatchPeer.COMMENT_COLLECTION)) {
            addToWrapperCollection(DispatchPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().add(DispatchPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(DispatchPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(DispatchPeer.COMMENT_COLLECTION)) {
            removeFromWrapperCollection(DispatchPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().remove(DispatchPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchPeer.COMMENT_COLLECTION, commentCollection);
    }

   @SuppressWarnings("unchecked")
    public CenterWrapper<?> getReceiverCenter() {
        boolean notCached = !isPropertyCached(DispatchPeer.RECEIVER_CENTER);
        CenterWrapper <?>receiverCenter = getWrappedProperty(DispatchPeer.RECEIVER_CENTER, CenterWrapper.class);
        if (receiverCenter != null && notCached) ((CenterBaseWrapper<?>) receiverCenter).addToDstDispatchCollectionInternal(Arrays.asList(this));
        return receiverCenter;
    }

    public void setReceiverCenter(CenterBaseWrapper<?> receiverCenter) {
        if (isInitialized(DispatchPeer.RECEIVER_CENTER)) {
            CenterBaseWrapper<?> oldReceiverCenter = getReceiverCenter();
            if (oldReceiverCenter != null) oldReceiverCenter.removeFromDstDispatchCollectionInternal(Arrays.asList(this));
        }
        if (receiverCenter != null) receiverCenter.addToDstDispatchCollectionInternal(Arrays.asList(this));
        setWrappedProperty(DispatchPeer.RECEIVER_CENTER, receiverCenter);
    }

    void setReceiverCenterInternal(CenterBaseWrapper<?> receiverCenter) {
        setWrappedProperty(DispatchPeer.RECEIVER_CENTER, receiverCenter);
    }

    public List<DispatchSpecimenWrapper> getDispatchSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION);
        List<DispatchSpecimenWrapper> dispatchSpecimenCollection = getWrapperCollection(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION, DispatchSpecimenWrapper.class, sort);
        if (notCached) {
            for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
                e.setDispatchInternal(this);
            }
        }
        return dispatchSpecimenCollection;
    }

    public void addToDispatchSpecimenCollection(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        addToWrapperCollection(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
            e.setDispatchInternal(this);
        }
    }

    void addToDispatchSpecimenCollectionInternal(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        if (isInitialized(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION)) {
            addToWrapperCollection(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        } else {
            getElementQueue().add(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        }
    }

    public void removeFromDispatchSpecimenCollection(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        removeFromWrapperCollection(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
            e.setDispatchInternal(null);
        }
    }

    void removeFromDispatchSpecimenCollectionInternal(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        if (isPropertyCached(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION)) {
            removeFromWrapperCollection(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        } else {
            getElementQueue().remove(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        }
    }

    public void removeFromDispatchSpecimenCollectionWithCheck(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
        for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
            e.setDispatchInternal(null);
        }
    }

    void removeFromDispatchSpecimenCollectionWithCheckInternal(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchPeer.DISPATCH_SPECIMEN_COLLECTION, dispatchSpecimenCollection);
    }

   @SuppressWarnings("unchecked")
    public CenterWrapper<?> getSenderCenter() {
        boolean notCached = !isPropertyCached(DispatchPeer.SENDER_CENTER);
        CenterWrapper <?>senderCenter = getWrappedProperty(DispatchPeer.SENDER_CENTER, CenterWrapper.class);
        if (senderCenter != null && notCached) ((CenterBaseWrapper<?>) senderCenter).addToSrcDispatchCollectionInternal(Arrays.asList(this));
        return senderCenter;
    }

    public void setSenderCenter(CenterBaseWrapper<?> senderCenter) {
        if (isInitialized(DispatchPeer.SENDER_CENTER)) {
            CenterBaseWrapper<?> oldSenderCenter = getSenderCenter();
            if (oldSenderCenter != null) oldSenderCenter.removeFromSrcDispatchCollectionInternal(Arrays.asList(this));
        }
        if (senderCenter != null) senderCenter.addToSrcDispatchCollectionInternal(Arrays.asList(this));
        setWrappedProperty(DispatchPeer.SENDER_CENTER, senderCenter);
    }

    void setSenderCenterInternal(CenterBaseWrapper<?> senderCenter) {
        setWrappedProperty(DispatchPeer.SENDER_CENTER, senderCenter);
    }

}
