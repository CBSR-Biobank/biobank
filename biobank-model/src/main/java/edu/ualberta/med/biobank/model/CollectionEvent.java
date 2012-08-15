package edu.ualberta.med.biobank.model;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Audited
@Entity
@Table(name = "COLLECTION_EVENT", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "PATIENT_ID", "COLLECTION_EVENT_TYPE_ID",
        "VISIT_NUMBER" })
})
@Unique(properties = { "patient", "type", "visitNumber" }, groups = PrePersist.class)
@NotUsed(by = SpecimenToCollectionEvent.class, property = "visit", groups = PreDelete.class)
public class CollectionEvent extends AbstractModel
    implements HasComments {
    private static final long serialVersionUID = 1L;

    private Patient patient;
    private CollectionEventType type;
    private Integer visitNumber;
    private Set<Comment> comments = new HashSet<Comment>(0);

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

    @Override
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "COLLECTION_EVENT_COMMENT",
        joinColumns = { @JoinColumn(name = "COLLECTION_EVENT_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
