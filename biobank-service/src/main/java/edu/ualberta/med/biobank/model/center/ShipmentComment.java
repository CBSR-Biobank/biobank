package edu.ualberta.med.biobank.model.center;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.Comment;

@Entity
@Table(name = "SHIPMENT_COMMENT")
public class ShipmentComment
    extends Comment<Shipment> {
    private static final long serialVersionUID = 1L;

    private Shipment shipment;

    @NotNull(message = "{ShipmentComment.shipment.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPMENT_ID", nullable = false)
    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    @Override
    @Transient
    public Shipment getOwner() {
        return getShipment();
    }
}
