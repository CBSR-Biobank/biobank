package edu.ualberta.med.biobank.model.study;

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

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.center.CenterLocation;
import edu.ualberta.med.biobank.model.center.Shipment;

/**
 * Represents a {@link Study}'s order to move a list of specific
 * {@link Specimen}s (via {@link RequestSpecimen}), wherever they're located, to
 * a given {@link CenterLocation}. A {@link Request} can result in one or more
 * {@link Shipment}s being created to track distinct transfers from one
 * {@link CenterLocation} to another.
 * 
 * @author Jonathan Ferland
 * @see RequestSpecimen
 */
@Audited
@Entity
@Table(name = "REQUEST")
public class Request
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private Long timeSubmitted;
    private CenterLocation toLocation;
    private Set<Shipment> shipments = new HashSet<Shipment>(0);

    @NotNull(message = "{Request.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @Column(name = "TIME_SUBMITTED")
    public Long getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(Long timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }

    @NotNull(message = "{Request.toLocation.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_CENTER_LOCATION_ID", nullable = false)
    public CenterLocation getToLocation() {
        return toLocation;
    }

    public void setToLocation(CenterLocation toLocation) {
        this.toLocation = toLocation;
    }

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "REQUEST_SHIPMENT",
        joinColumns = { @JoinColumn(name = "REQUEST_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "SHIPMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Shipment> getShipments() {
        return this.shipments;
    }

    public void setShipments(Set<Shipment> shipments) {
        this.shipments = shipments;
    }
}
