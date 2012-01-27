package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class OriginInfo extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Collection<Comment> commentCollection = new HashSet<Comment>();
    private Collection<Specimen> specimenCollection = new HashSet<Specimen>();
    private ShipmentInfo shipmentInfo;
    private Center center;
    private Site receiverSite;

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    public Collection<Specimen> getSpecimenCollection() {
        return specimenCollection;
    }

    public void setSpecimenCollection(Collection<Specimen> specimenCollection) {
        this.specimenCollection = specimenCollection;
    }

    public ShipmentInfo getShipmentInfo() {
        return shipmentInfo;
    }

    public void setShipmentInfo(ShipmentInfo shipmentInfo) {
        this.shipmentInfo = shipmentInfo;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public Site getReceiverSite() {
        return receiverSite;
    }

    public void setReceiverSite(Site receiverSite) {
        this.receiverSite = receiverSite;
    }
}
