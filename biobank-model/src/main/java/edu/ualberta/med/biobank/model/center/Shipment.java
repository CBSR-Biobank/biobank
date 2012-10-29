package edu.ualberta.med.biobank.model.center;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.type.ShipmentState;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * Represents a transfer of {@link Specimen}s and/or {@link Container}s from one
 * {@link CenterLocation} to another.
 * 
 * @author Jonathan Ferland
 * @see ShipmentSpecimen
 * @see ShipmentContainer
 */
@Audited
@Entity
@Table(name = "SHIPMENT")
@NotUsed.List({
    @NotUsed(by = ShipmentSpecimen.class, property = "shipment", groups = PreDelete.class),
    @NotUsed(by = ShipmentContainer.class, property = "shipment", groups = PreDelete.class)
})
public class Shipment
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private ShipmentState state;
    private CenterLocation fromLocation;
    private CenterLocation toLocation;
    private ShipmentData data;
    private Long timePacked;
    private Long timeSent;
    private Long timeReceived;
    private Long timeUnpacked;

    @NotNull(message = "{Shipment.state.NotNull}")
    @Type(type = "shipmentState")
    @Column(name = "STATE", length = 1)
    public ShipmentState getState() {
        return this.state;
    }

    public void setState(ShipmentState state) {
        this.state = state;
    }

    @NotNull(message = "{Shipment.fromLocation.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_CENTER_LOCATION_ID", nullable = false)
    public CenterLocation getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(CenterLocation fromLocation) {
        this.fromLocation = fromLocation;
    }

    @NotNull(message = "{Shipment.toLocation.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_CENTER_LOCATION_ID", nullable = false)
    public CenterLocation getToLocation() {
        return toLocation;
    }

    public void setToLocation(CenterLocation toLocation) {
        this.toLocation = toLocation;
    }

    @Valid
    @Embedded
    public ShipmentData getData() {
        return this.data;
    }

    public void setData(ShipmentData data) {
        this.data = data;
    }

    @Column(name = "TIME_PACKED")
    public Long getTimePacked() {
        return timePacked;
    }

    public void setTimePacked(Long timePacked) {
        this.timePacked = timePacked;
    }

    @Column(name = "TIME_SENT")
    public Long getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Long timeSent) {
        this.timeSent = timeSent;
    }

    @Column(name = "TIME_RECEIVED")
    public Long getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(Long timeReceived) {
        this.timeReceived = timeReceived;
    }

    @Column(name = "TIME_UNPACKED")
    public Long getTimeUnpacked() {
        return timeUnpacked;
    }

    public void setTimeUnpacked(Long timeUnpacked) {
        this.timeUnpacked = timeUnpacked;
    }
}