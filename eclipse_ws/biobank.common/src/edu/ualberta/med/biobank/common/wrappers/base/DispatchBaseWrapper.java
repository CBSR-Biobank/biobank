/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.type.DispatchState;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

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

    public DispatchState getState() {
        return getProperty(DispatchPeer.STATE);
    }

    public void setState(DispatchState state) {
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
        List<CommentWrapper> commentCollection = getWrapperCollection(DispatchPeer.COMMENTS, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(DispatchPeer.COMMENTS, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(DispatchPeer.COMMENTS)) {
            addToWrapperCollection(DispatchPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().add(DispatchPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(DispatchPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(DispatchPeer.COMMENTS)) {
            removeFromWrapperCollection(DispatchPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().remove(DispatchPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchPeer.COMMENTS, commentCollection);
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
        boolean notCached = !isPropertyCached(DispatchPeer.DISPATCH_SPECIMENS);
        List<DispatchSpecimenWrapper> dispatchSpecimenCollection = getWrapperCollection(DispatchPeer.DISPATCH_SPECIMENS, DispatchSpecimenWrapper.class, sort);
        if (notCached) {
            for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
                e.setDispatchInternal(this);
            }
        }
        return dispatchSpecimenCollection;
    }

    public void addToDispatchSpecimenCollection(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        addToWrapperCollection(DispatchPeer.DISPATCH_SPECIMENS, dispatchSpecimenCollection);
        for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
            e.setDispatchInternal(this);
        }
    }

    void addToDispatchSpecimenCollectionInternal(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        if (isInitialized(DispatchPeer.DISPATCH_SPECIMENS)) {
            addToWrapperCollection(DispatchPeer.DISPATCH_SPECIMENS, dispatchSpecimenCollection);
        } else {
            getElementQueue().add(DispatchPeer.DISPATCH_SPECIMENS, dispatchSpecimenCollection);
        }
    }

    public void removeFromDispatchSpecimenCollection(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        removeFromWrapperCollection(DispatchPeer.DISPATCH_SPECIMENS, dispatchSpecimenCollection);
        for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
            e.setDispatchInternal(null);
        }
    }

    void removeFromDispatchSpecimenCollectionInternal(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) {
        if (isPropertyCached(DispatchPeer.DISPATCH_SPECIMENS)) {
            removeFromWrapperCollection(DispatchPeer.DISPATCH_SPECIMENS, dispatchSpecimenCollection);
        } else {
            getElementQueue().remove(DispatchPeer.DISPATCH_SPECIMENS, dispatchSpecimenCollection);
        }
    }

    public void removeFromDispatchSpecimenCollectionWithCheck(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchPeer.DISPATCH_SPECIMENS, dispatchSpecimenCollection);
        for (DispatchSpecimenBaseWrapper e : dispatchSpecimenCollection) {
            e.setDispatchInternal(null);
        }
    }

    void removeFromDispatchSpecimenCollectionWithCheckInternal(List<? extends DispatchSpecimenBaseWrapper> dispatchSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(DispatchPeer.DISPATCH_SPECIMENS, dispatchSpecimenCollection);
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
