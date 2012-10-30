package edu.ualberta.med.biobank.model.study;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;
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
@Table(name = "COLLECTION_EVENT")
@Unique(properties = { "patient", "type", "visitNumber" }, groups = PrePersist.class)
@NotUsed(by = SpecimenCollectionEvent.class, property = "collectionEvent", groups = PreDelete.class)
public class CollectionEvent
    extends VersionedLongIdModel {
    private static final long serialVersionUID = 1L;

    public static final int MIN_VISIT_NUMBER = 1;

    private Patient patient;
    private CollectionEventType type;
    private Integer visitNumber;
    private Long timeDone;

    @NaturalId
    @NotNull(message = "{CollectionEvent.patient.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @NaturalId
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "{CollectionEvent.type.NotNull}")
    @JoinColumn(name = "COLLECTION_EVENT_TYPE_ID", nullable = false)
    public CollectionEventType getType() {
        return type;
    }

    public void setType(CollectionEventType type) {
        this.type = type;
    }

    @NaturalId
    @Min(value = MIN_VISIT_NUMBER, message = "{CollectionEvent.visitNumber.Min}")
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
     * {@link Specimen}s, then the time (in milliseconds) it occurred at must be
     * able to be stored on the {@link CollectionEvent} itself.
     * 
     * @return when this {@link CollectionEvent} occurred.
     */
    @NotNull(message = "{CollectionEvent.timeDone.NotNull}")
    @Past(message = "{CollectionEvent.timeDone.Past}")
    @Column(name = "TIME_DONE", nullable = false)
    public Long getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(Long timeDone) {
        this.timeDone = timeDone;
    }
}
