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
@Table(name = "SHIPMENT_CONTAINER_COMMENT")
public class ShipmentContainerComment
    extends Comment<ShipmentContainer> {
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

    @Override
    @Transient
    public ShipmentContainer getOwner() {
        return getShipmentContainer();
    }
}
