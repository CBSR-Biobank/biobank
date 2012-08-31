package edu.ualberta.med.biobank.model;

import java.util.Date;
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

/**
 * caTissue Term - Specimen Distribution: An event that results in transfer of a
 * specimen from a Repository to a Laboratory
 * 
 */
@Audited
@Entity
@Table(name = "REQUEST")
public class Request
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Study study;
    private Date timeSubmitted;
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
    public Date getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(Date timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
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
