package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Represents a visit made to a {@link Patient} during which {@link Specimen}s
 * may have been drawn and other information may have been collected.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "COLLECTION_EVENT",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {
            "PATIENT_ID",
            "COLLECTION_EVENT_TYPE_ID",
            "VISIT_NUMBER" })
    })
@Unique(properties = { "patient", "type", "visitNumber" }, groups = PrePersist.class)
@NotUsed(by = SpecimenCollectionEvent.class, property = "collectionEvent", groups = PreDelete.class)
public class CollectionEvent
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Patient patient;
    private CollectionEventType type;
    private Integer visitNumber;
    private Date timeDone;

    @NotNull(message = "{CollectionEvent.patient.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "{CollectionEvent.type.NotNull}")
    @JoinColumn(name = "COLLECTION_EVENT_TYPE_ID", nullable = false)
    public CollectionEventType getType() {
        return type;
    }

    public void setType(CollectionEventType type) {
        this.type = type;
    }

    @Min(value = 1, message = "{CollectionEvent.visitNumber.Min}")
    @NotNull(message = "{CollectionEvent.visitNumber.NotNull}")
    @Column(name = "VISIT_NUMBER", nullable = false)
    public Integer getVisitNumber() {
        return this.visitNumber;
    }

    public void setVisitNumber(Integer visitNumber) {
        this.visitNumber = visitNumber;
    }

    /**
     * If a {@link CollectionEvent} does not have any associated
     * {@link Specimen}s, then the time it occurred at must be able to be stored
     * on the {@link CollectionEvent} itself.
     * 
     * @return when this {@link CollectionEvent} occurred.
     */
    @NotNull(message = "{CollectionEvent.timeDone.NotNull}")
    @Past(message = "{CollectionEvent.timeDone.Past}")
    @Column(name = "TIME_DONE", nullable = false)
    public Date getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(Date timeDone) {
        this.timeDone = timeDone;
    }
}
