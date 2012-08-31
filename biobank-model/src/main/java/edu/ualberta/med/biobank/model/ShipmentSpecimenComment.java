package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SHIPMENT_SPECIMEN_COMMENT")
public class ShipmentSpecimenComment
    extends Comment {
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
}
