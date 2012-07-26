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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * caTissue Term - Transfer Event: Event that refers to moving specimen from one
 * storage location to another storage location.
 * 
 */
@Audited
@Entity
@Table(name = "DISPATCH")
@NotUsed(by = DispatchSpecimen.class, property = "dispatch", groups = PreDelete.class)
public class Dispatch extends AbstractModel
    implements HasComments {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Dispatch",
        "Dispatches");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString RECEIVER_CENTER = bundle.trc(
            "model",
            "Receiver").format();
        public static final LString SENDER_CENTER = bundle.trc(
            "model",
            "Sender").format();
        public static final LString STATE = bundle.trc(
            "model",
            "State").format();
    }

    private DispatchState state = DispatchState.CREATION;
    private Center senderCenter;
    private Center receiverCenter;
    private ShipmentInfo shipmentInfo;
    private Set<Comment> comments = new HashSet<Comment>(0);

    @NotNull(message = "{Dispatch.state.NotNull}")
    @Column(name = "STATE")
    @Type(type = "dispatchState")
    public DispatchState getState() {
        return this.state;
    }

    public void setState(DispatchState state) {
        this.state = state;
    }

    @NotNull(message = "{Dispatch.senderCenter.NotNull}")
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

    @NotNull(message = "{Dispatch.receiverCenter.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_CENTER_ID", nullable = false)
    public Center getReceiverCenter() {
        return this.receiverCenter;
    }

    public void setReceiverCenter(Center receiverCenter) {
        this.receiverCenter = receiverCenter;
    }

    @Override
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "DISPATCH_COMMENT",
        joinColumns = { @JoinColumn(name = "DISPATCH_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

}
