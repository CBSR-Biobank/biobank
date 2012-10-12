package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.ShipmentItemState;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "SHIPMENT_CONTAINER",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "SHIPMENT_ID", "CONTAINER_ID" })
    })
@Unique(properties = { "shipment", "container" }, groups = PrePersist.class)
public class ShipmentContainer
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Shipment shipment;
    private Container container;
    private ShipmentItemState state;

    @NotNull(message = "{ShipmentContainer.shipment.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPMENT_ID", nullable = false)
    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    @NotNull(message = "{ShipmentContainer.container.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTAINER_ID", nullable = false)
    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @NotNull(message = "{ShipmentContainer.state.NotNull}")
    @Type(type = "shipmentItemState")
    @Column(name = "STATE", length = 1)
    public ShipmentItemState getState() {
        return this.state;
    }

    public void setState(ShipmentItemState state) {
        this.state = state;
    }
}
