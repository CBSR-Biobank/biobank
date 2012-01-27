package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class Dispatch extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer state; // TODO: convert to enum
    private Collection<DispatchSpecimen> dispatchSpecimenCollection =
        new HashSet<DispatchSpecimen>();
    private Center senderCenter;
    private ShipmentInfo shipmentInfo;
    private Center receiverCenter;
    private Collection<Comment> commentCollection = new HashSet<Comment>();

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Collection<DispatchSpecimen> getDispatchSpecimenCollection() {
        return dispatchSpecimenCollection;
    }

    public void setDispatchSpecimenCollection(
        Collection<DispatchSpecimen> dispatchSpecimenCollection) {
        this.dispatchSpecimenCollection = dispatchSpecimenCollection;
    }

    public Center getSenderCenter() {
        return senderCenter;
    }

    public void setSenderCenter(Center senderCenter) {
        this.senderCenter = senderCenter;
    }

    public ShipmentInfo getShipmentInfo() {
        return shipmentInfo;
    }

    public void setShipmentInfo(ShipmentInfo shipmentInfo) {
        this.shipmentInfo = shipmentInfo;
    }

    public Center getReceiverCenter() {
        return receiverCenter;
    }

    public void setReceiverCenter(Center receiverCenter) {
        this.receiverCenter = receiverCenter;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }
}
