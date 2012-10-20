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
@Table(name = "SHIPMENT_SPECIMEN_COMMENT")
public class ShipmentSpecimenComment
    extends Comment<ShipmentSpecimen> {
    private static final long serialVersionUID = 1L;

    private ShipmentSpecimen shipmentSpecimen;

    @NotNull(message = "{ShipmentSpecimenComment.shipmentSpecimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPMENT_SPECIMEN_ID", nullable = false)
    public ShipmentSpecimen getShipmentSpecimen() {
        return shipmentSpecimen;
    }

    public void setShipmentSpecimen(ShipmentSpecimen shipmentSpecimen) {
        this.shipmentSpecimen = shipmentSpecimen;
    }

    @Override
    @Transient
    public ShipmentSpecimen getOwner() {
        return getShipmentSpecimen();
    }
}
