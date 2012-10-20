package edu.ualberta.med.biobank.model.center;

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

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.type.ShipmentItemState;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "SHIPMENT_SPECIMEN",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "SHIPMENT_ID", "SPECIMEN_ID" })
    })
@Unique(properties = { "shipment", "specimen" }, groups = PrePersist.class)
public class ShipmentSpecimen
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Shipment shipment;
    private Specimen specimen;
    private ShipmentItemState state;
    private ShipmentContainer shipmentContainer;

    @NotNull(message = "{ShipmentSpecimen.shipment.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPMENT_ID", nullable = false)
    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    @NotNull(message = "{ShipmentSpecimen.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return this.specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @NotNull(message = "{ShipmentSpecimen.state.NotNull}")
    @Type(type = "shipmentItemState")
    @Column(name = "STATE", length = 1)
    public ShipmentItemState getState() {
        return this.state;
    }

    public void setState(ShipmentItemState state) {
        this.state = state;
    }

    /**
     * @return the {@link ShipmentContainer} this {@link ShipmentSpecimen} is
     *         in, for this {@link #shipment}, or null if the {@link #specimen}
     *         has no {@link Container}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public ShipmentContainer getShipmentContainer() {
        return shipmentContainer;
    }

    public void setShipmentContainer(ShipmentContainer shipmentContainer) {
        this.shipmentContainer = shipmentContainer;
    }
}
