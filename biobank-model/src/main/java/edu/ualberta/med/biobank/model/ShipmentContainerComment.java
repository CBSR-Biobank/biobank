package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SHIPMENT_CONTAINER_COMMENT")
public class ShipmentContainerComment
    extends Comment {
    private static final long serialVersionUID = 1L;

    private ShipmentContainer shipmentContainer;

    @NotNull(message = "{ShipmentContainerComment.shipmentContainer.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPMENT_CONTAINER_ID", nullable = false)
    public ShipmentContainer getShipmentContainer() {
        return shipmentContainer;
    }

    public void setShipmentContainer(ShipmentContainer shipmentContainer) {
        this.shipmentContainer = shipmentContainer;
    }
}
