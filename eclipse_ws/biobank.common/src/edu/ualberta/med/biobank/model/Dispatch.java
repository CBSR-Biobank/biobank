package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * caTissue Term - Transfer Event: Event that refers to moving specimen from one
 * storage location to another storage location.
 * 
 */
@Entity
@Table(name = "DISPATCH")
public class Dispatch extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer state;
    private Set<DispatchSpecimen> dispatchSpecimens =
        new HashSet<DispatchSpecimen>(0);
    private Center senderCenter;
    private ShipmentInfo shipmentInfo;
    private Center receiverCenter;
    private Set<Comment> comments = new HashSet<Comment>(0);

    // TODO: convert to enum
    @NotNull(message = "{edu.ualberta.med.biobank.model.Dispatch.state.NotNull}")
    @Column(name = "STATE")
    public Integer getState() {
        return this.state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dispatch")
    public Set<DispatchSpecimen> getDispatchSpecimens() {
        return this.dispatchSpecimens;
    }

    public void setDispatchSpecimens(Set<DispatchSpecimen> dispatchSpecimens) {
        this.dispatchSpecimens = dispatchSpecimens;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Dispatch.senderCenter.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_CENTER_ID", nullable = false)
    public Center getSenderCenter() {
        return this.senderCenter;
    }

    public void setSenderCenter(Center senderCenter) {
        this.senderCenter = senderCenter;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPMENT_INFO_ID", unique = true)
    public ShipmentInfo getShipmentInfo() {
        return this.shipmentInfo;
    }

    public void setShipmentInfo(ShipmentInfo shipmentInfo) {
        this.shipmentInfo = shipmentInfo;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Dispatch.receiverCenter.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_CENTER_ID", nullable = false)
    public Center getReceiverCenter() {
        return this.receiverCenter;
    }

    public void setReceiverCenter(Center receiverCenter) {
        this.receiverCenter = receiverCenter;
    }

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "DISPATCH_COMMENT",
        joinColumns = { @JoinColumn(name = "DISPATCH_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
